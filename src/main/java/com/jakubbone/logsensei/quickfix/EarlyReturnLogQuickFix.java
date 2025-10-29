package com.jakubbone.logsensei.quickfix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiBlockStatement;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class EarlyReturnLogQuickFix implements LocalQuickFix {
    private final String lombok_log4J_annotation = "lombok.extern.log4j.Log4j2";

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return "Add DEBUG log before early return";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
        PsiElement returnKeyword = problemDescriptor.getPsiElement();
        PsiReturnStatement returnStatement = PsiTreeUtil.getParentOfType(returnKeyword, PsiReturnStatement.class);
        if (returnStatement == null) {
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
        String methodName = containingMethod.getName();


        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

        // Create log statement
        String logStatementText = String.format(
                "log.debug(\"[%s] Early return\");",
                methodName
        );

        addLog4jAnnotationAndImports(project, containingClass);

        // Create log statement
        PsiStatement logStatement = factory.createStatementFromText(logStatementText, returnStatement);

        PsiElement parent = returnStatement.getParent();
        // Add the statement to existing or new created block
        if(parent instanceof PsiCodeBlock){
            // Existing block { }
            PsiCodeBlock codeBlock = (PsiCodeBlock) parent;
            codeBlock.addBefore(logStatement, returnStatement);
        } else {
            // Single statement without { }
            // Example: if (x == null) return; → if (x == null) { log.debug(...); return; }
            String blockText = String.format(
                    "{ %s %s }",
                    logStatementText,
                    returnStatement.getText()
            );
            PsiBlockStatement newBlock = (PsiBlockStatement) factory.createStatementFromText(blockText, parent);
            returnStatement.replace(newBlock);
        }
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
