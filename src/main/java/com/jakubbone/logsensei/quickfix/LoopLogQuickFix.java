package com.jakubbone.logsensei.quickfix;

import java.util.List;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class LoopLogQuickFix implements LocalQuickFix {
    private final String lombok_log4J_annotation = "lombok.extern.log4j.Log4j2";

    private final List<PsiMethodCallExpression> problematicLogs;

    public LoopLogQuickFix(List<PsiMethodCallExpression> problematicLogs) {
        this.problematicLogs = problematicLogs;
    }


    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return "Add debug log";
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

        addLog4jAnnotationAndImports(project, containingClass);

        for(PsiMethodCallExpression logCall: problematicLogs){
            if(!logCall.isValid()){
                continue;
            }
            changeLogLevelToDebug(project, logCall);
        }
    }

    private void changeLogLevelToDebug(@NotNull Project project, @NotNull PsiMethodCallExpression logCall) {
        PsiReferenceExpression logExpression = logCall.getMethodExpression();
        String currentText = logExpression.getText();
        String newText = currentText.replace(".info", ".debug");

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        PsiExpression newExpression = factory.createExpressionFromText(newText, logCall);
        logExpression.replace(newExpression);

    }


    private void addLog4jAnnotationAndImports(@NotNull Project project, @NotNull PsiClass psiClass) {
        PsiModifierList modifierList = psiClass.getModifierList();
        if (modifierList == null || modifierList.hasAnnotation(lombok_log4J_annotation)) {
            return;
        }

        // Add annotation
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        PsiAnnotation annotation = factory.createAnnotationFromText("@" + lombok_log4J_annotation, psiClass);
        modifierList.addBefore(annotation, modifierList.getFirstChild());

        // Add imports and shorten class names
        JavaCodeStyleManager.getInstance(project).shortenClassReferences(psiClass.getContainingFile());
    }
}