package com.jakubbone.logsensei.quickfix;

import static com.jakubbone.logsensei.utils.LogSenseiConstants.LOG_PATTERN_DEBUG;
import static com.jakubbone.logsensei.utils.LogSenseiUtils.addLog4jAnnotationAndImports;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiBlockStatement;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiStatement;
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
                LOG_PATTERN_DEBUG,
                methodName
        );

        // Add Lombok annotation
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
}
