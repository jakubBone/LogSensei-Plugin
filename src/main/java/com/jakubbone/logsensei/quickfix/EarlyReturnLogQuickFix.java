package com.jakubbone.logsensei.quickfix;

import static com.jakubbone.logsensei.psi.LogImplementationService.implementLoggingSolution;
import static com.jakubbone.logsensei.psi.LogImplementationService.resolveLoggerFieldName;
import static com.jakubbone.logsensei.psi.LogStatementFactory.createDebugLog;
import static com.jakubbone.logsensei.education.LogEducationNotifier.showDebugLevelEducation;
import static com.jakubbone.logsensei.psi.LogStatementFactory.createErrorLog;
import static com.jakubbone.logsensei.psi.PsiStatementUtils.addLogBeforeStatement;
import static com.jakubbone.logsensei.dependency.ui.DependencyDialogService.askUserForLibraryAndAnnotation;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiCatchSection;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;
import org.jetbrains.annotations.NotNull;


public class EarlyReturnLogQuickFix implements LocalQuickFix {

    private final LoggingLibrary library;

    public EarlyReturnLogQuickFix() {
        this(LoggingLibrary.NONE);
    }

    public EarlyReturnLogQuickFix(LoggingLibrary library) {
        this.library = library;
    }

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return library == LoggingLibrary.JAVA_UTIL_LOGGING
                ? "Add FINE log before early return"
                : "Add DEBUG log before early return";
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
                PsiElement returnKeyword = descriptor.getPsiElement();
                addLog(project, returnKeyword, lib);
            });
            showDebugLevelEducation(project, lib);
        }
    }

    public void addLog(Project project, PsiElement returnKeyword, LoggingLibrary lib){
        PsiReturnStatement returnStmt = PsiTreeUtil.getParentOfType(returnKeyword, PsiReturnStatement.class);
        if (returnStmt == null) {
            return;
        }

        PsiClass containingClass = PsiTreeUtil.getParentOfType(returnKeyword, PsiClass.class);
        if(containingClass == null){
            return;
        }

        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(returnKeyword, PsiMethod.class);
        if(containingMethod == null){
            return;
        }

        implementLoggingSolution(project, containingClass, lib);

        String loggerName = resolveLoggerFieldName(containingClass);
        PsiStatement logStmt = createDebugLog(
                project,
                loggerName,
                containingClass.getName(),
                returnStmt,
                lib
        );

        addLogBeforeStatement(project, logStmt, returnStmt);

        if (lib == LoggingLibrary.JAVA_UTIL_LOGGING) {
            JavaCodeStyleManager.getInstance(project)
                    .shortenClassReferences(containingClass.getContainingFile());
        }
    }
}