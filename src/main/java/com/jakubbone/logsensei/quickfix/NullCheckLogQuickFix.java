package com.jakubbone.logsensei.quickfix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class NullCheckLogQuickFix implements LocalQuickFix {
    private final String variableName;
    private static final String LOMBOK_LOG4J2_ANNOTATION = "lombok.extern.log4j.Log4j2";


    public NullCheckLogQuickFix(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return "Add warning log for null check";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
        PsiElement psiElement = problemDescriptor.getPsiElement();
        if (psiElement == null) return;

        PsiIfStatement ifStatement = PsiTreeUtil.getParentOfType(psiElement, PsiIfStatement.class);
        if (ifStatement == null) return;

        PsiStatement thenBranch = ifStatement.getThenBranch();
        if (thenBranch == null) return;


        // 3. Find the class containing catch section to add the annotation
        PsiClass containingClass = PsiTreeUtil.getParentOfType(ifStatement, PsiClass.class);
        if (containingClass == null) return;

        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(ifStatement, PsiMethod.class);
        if (containingMethod == null) return;
        String methodName = containingMethod.getName();


        addLog4jAnnotationAndImports(project, containingClass);

        // Create the log statement
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

        String logStatementText = String.format(
                "log.warn(\"[%s] Variable '%s' is null\", \"%s\");",
                methodName,
                variableName,
                variableName
        );

        PsiStatement logStatement = factory.createStatementFromText(logStatementText, thenBranch);

        // Add the statement to existing or new created block
        if (thenBranch instanceof PsiBlockStatement) {
            // Existing block
            PsiBlockStatement blockStatement = (PsiBlockStatement) thenBranch;
            PsiCodeBlock codeBlock = blockStatement.getCodeBlock();

            // Add log as a first statement in the block
            PsiStatement[] statements = codeBlock.getStatements();
            if (statements.length > 0) {
                codeBlock.addBefore(logStatement, statements[0]);
            } else {
                codeBlock.add(logStatement);
            }
        } else {
            // Single statement - a new block creating
            PsiBlockStatement newBlock = (PsiBlockStatement) factory.createStatementFromText("{}", thenBranch);
            PsiCodeBlock codeBlock = newBlock.getCodeBlock();

            // Add log statement
            codeBlock.add(logStatement);
            // Add original statement
            codeBlock.add(thenBranch.copy());

            // Replace the original statement with the new block
            thenBranch.replace(newBlock);
        }

    }

    private void addLog4jAnnotationAndImports(@NotNull Project project, @NotNull PsiClass psiClass){
        PsiModifierList modifierList = psiClass.getModifierList();
        if(modifierList == null || modifierList.hasAnnotation(LOMBOK_LOG4J2_ANNOTATION)){
            return;
        }

        // Add annotation
        PsiElementFactory factory =  JavaPsiFacade.getElementFactory(project);
        PsiAnnotation annotation = factory.createAnnotationFromText("@"+LOMBOK_LOG4J2_ANNOTATION, psiClass);
        modifierList.addBefore(annotation, modifierList.getFirstChild());

        // Add imports and shorten class names
        JavaCodeStyleManager.getInstance(project).shortenClassReferences(psiClass.getContainingFile());
    }
}
