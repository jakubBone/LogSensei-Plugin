package com.jakubbone.logsensei.quickfix;

import static com.jakubbone.logsensei.education.LogEducationNotifier.showDebugLevelEducation;
import static com.jakubbone.logsensei.psi.LogImplementationService.implementLoggingSolution;
import static com.jakubbone.logsensei.dependency.ui.DependencyDialogService.askUserForLibraryAndAnnotation;
import java.util.List;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
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

    public LoopLogQuickFix(List<PsiMethodCallExpression> problematicLogs) {
        this.problematicLogs = problematicLogs;
    }

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return "Change log level: INFO -> DEBUG";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        LoggingLibrary lib = askUserForLibraryAndAnnotation(project);
        if(lib != null){
            PsiElement loopKeyword = descriptor.getPsiElement();
            addLog(project, loopKeyword, lib);
            showDebugLevelEducation(project);
        }
    }

    private void addLog(Project project, PsiElement loopKeyword, LoggingLibrary lib) {
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
            changeLogLevelToDebug(project, logCall);
        }
    }

    /*@Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {

        LoggingLibrary selectedLibrary = askUserForLibraryAndAnnotation(project);
        if(selectedLibrary == null){
            return;
        }

        PsiElement psiElement = problemDescriptor.getPsiElement();
        if (psiElement == null) {
            return;
        }

        PsiClass containingClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
        if (containingClass == null) {
            return;
        }

        implementLoggingSolution(project, containingClass, selectedLibrary);

        for(PsiMethodCallExpression logCall: problematicLogs){
            if(!logCall.isValid()){
                continue;
            }
            changeLogLevelToDebug(project, logCall);
        }
        showDebugLevelEducation(project);
    }*/

    private void changeLogLevelToDebug(@NotNull Project project, @NotNull PsiMethodCallExpression logCall) {
        PsiReferenceExpression logExpression = logCall.getMethodExpression();
        String currentText = logExpression.getText();
        String newText = currentText.replace(".info", ".debug");

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        PsiExpression newExpression = factory.createExpressionFromText(newText, logCall);
        logExpression.replace(newExpression);
    }
}