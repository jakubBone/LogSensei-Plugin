package com.jakubbone.logsensei.quickfix;

import static com.jakubbone.logsensei.utils.LogEducationNotifier.showDebugLevelEducation;
import static com.jakubbone.logsensei.utils.LogSenseiUtils.addLog4jAnnotationAndImports;
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
import com.jakubbone.logsensei.utils.LogEducationNotifier;
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
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
        PsiElement psiElement = problemDescriptor.getPsiElement(); // loop
        if (psiElement == null) {
            return;
        }

        PsiClass containingClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
        if (containingClass == null) { // Loop
            return;
        }

        // Add Lombok annotation
        addLog4jAnnotationAndImports(project, containingClass);

        for(PsiMethodCallExpression logCall: problematicLogs){
            if(!logCall.isValid()){
                continue;
            }
            changeLogLevelToDebug(project, logCall);
        }

        showDebugLevelEducation(project);
    }

    private void changeLogLevelToDebug(@NotNull Project project, @NotNull PsiMethodCallExpression logCall) {
        PsiReferenceExpression logExpression = logCall.getMethodExpression();
        String currentText = logExpression.getText();
        String newText = currentText.replace(".info", ".debug");

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        PsiExpression newExpression = factory.createExpressionFromText(newText, logCall);
        logExpression.replace(newExpression);
    }
}