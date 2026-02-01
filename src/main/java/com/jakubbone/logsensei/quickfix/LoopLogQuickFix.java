package com.jakubbone.logsensei.quickfix;

import static com.jakubbone.logsensei.education.LogEducationNotifier.showDebugLevelEducation;
import static com.jakubbone.logsensei.psi.LogImplementationService.implementLoggingSolution;
import static com.jakubbone.logsensei.dependency.ui.DependencyDialogService.askUserForLibraryAndAnnotation;
import java.util.List;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;
import org.jetbrains.annotations.NotNull;

public class LoopLogQuickFix implements LocalQuickFix {

    private final List<PsiMethodCallExpression> problematicLogs;
    private final LoggingLibrary library;

    public LoopLogQuickFix(List<PsiMethodCallExpression> problematicLogs) {
        this(problematicLogs, LoggingLibrary.NONE);
    }

    public LoopLogQuickFix(List<PsiMethodCallExpression> problematicLogs, LoggingLibrary library) {
        this.problematicLogs = problematicLogs;
        this.library = library;
    }

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return library == LoggingLibrary.JAVA_UTIL_LOGGING
                ? "Change log level: INFO -> FINE"
                : "Change log level: INFO -> DEBUG";
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiClass containingClass = PsiTreeUtil.getParentOfType(descriptor.getPsiElement(), PsiClass.class);
        LoggingLibrary lib = askUserForLibraryAndAnnotation(project, containingClass);
        if(lib != null){
            WriteCommandAction.runWriteCommandAction(project, () -> {
                PsiElement loopKeyword = descriptor.getPsiElement();
                addLog(project, loopKeyword, lib);
            });
            showDebugLevelEducation(project, lib);
        }
    }

    public void addLog(Project project, PsiElement loopKeyword, LoggingLibrary lib) {
        if (loopKeyword == null) {
            return;
        }

        PsiClass containingClass = PsiTreeUtil.getParentOfType(loopKeyword, PsiClass.class);
        if (containingClass == null) {
            return;
        }

        implementLoggingSolution(project, containingClass, lib);

        for(PsiMethodCallExpression logCall: problematicLogs){
            if(!logCall.isValid()){
                continue;
            }
            changeLogLevelToDebug(project, logCall, lib);
        }
    }

    private void changeLogLevelToDebug(@NotNull Project project, @NotNull PsiMethodCallExpression logCall, @NotNull LoggingLibrary library) {
        PsiReferenceExpression logExpression = logCall.getMethodExpression();
        String methodName = logExpression.getReferenceName();
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

        if ("log".equals(methodName)) {
            // .log(Level.INFO, ...) pattern — replace Level.INFO in the first argument
            PsiExpression[] args = logCall.getArgumentList().getExpressions();
            if (args.length > 0 && args[0].getText().contains("INFO")) {
                String argText = args[0].getText();
                String newArgText = (library == LoggingLibrary.JAVA_UTIL_LOGGING)
                        ? argText.replace("INFO", "FINE")
                        : argText.replace("INFO", "DEBUG");
                PsiExpression newArg = factory.createExpressionFromText(newArgText, logCall);
                args[0].replace(newArg);
            }
        } else {
            // .info(...) pattern — replace method name
            String currentText = logExpression.getText();
            String replacement = (library == LoggingLibrary.JAVA_UTIL_LOGGING) ? ".fine" : ".debug";
            String newText = currentText.replace(".info", replacement);
            PsiExpression newExpression = factory.createExpressionFromText(newText, logCall);
            logExpression.replace(newExpression);
        }
    }
}