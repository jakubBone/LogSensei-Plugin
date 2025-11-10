package com.jakubbone.logsensei.quickfix;

import static com.jakubbone.logsensei.utils.LogEducationNotifier.showWarnLevelEducation;
import static com.jakubbone.logsensei.utils.LogSenseiConstants.LOG_PATTERN_WARN;
import static com.jakubbone.logsensei.utils.LogSenseiUtils.addLog4jAnnotationAndImports;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class NullCheckLogQuickFix implements LocalQuickFix {
    private final String variableName;

    public NullCheckLogQuickFix(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return "Add WARN log for null check";
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

        // Add Lombok annotation
        addLog4jAnnotationAndImports(project, containingClass);

        // Create the log statement
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

        String logStatementText = String.format(
                LOG_PATTERN_WARN,
                methodName,
                variableName,
                variableName
        );

        PsiStatement logStatement = factory.createStatementFromText(logStatementText, thenBranch);

        // Add the statement to existing or new created block
        if (thenBranch instanceof PsiBlockStatement) {
            // Existing block { }
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
            // Single statement without { }
            // Example: if (x == null) return; → if (x == null) { log.warn(...); return; }
            String blockText = String.format(
                    "{ %s %s }",
                    logStatementText,
                    thenBranch.getText()
            );
            PsiBlockStatement newBlock = (PsiBlockStatement) factory.createStatementFromText(blockText, ifStatement);
            thenBranch.replace(newBlock);
        }

        showWarnLevelEducation(project);
    }
}
