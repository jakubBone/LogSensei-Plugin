package com.jakubbone.logsensei.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import com.jakubbone.logsensei.quickfix.NullCheckLogQuickFix;
import org.jetbrains.annotations.NotNull;

public class NullCheckInspection extends AbstractBaseJavaLocalInspectionTool {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {

        return new JavaElementVisitor() {
            @Override
            public void visitIfStatement(@NotNull PsiIfStatement statement) {
                super.visitIfStatement(statement);

                PsiExpression condition = statement.getCondition();
                if(condition == null) return;

                if(isNullComparison(condition)){
                    PsiStatement thenBranch = statement.getThenBranch();
                    if(thenBranch != null && !hasWarningLog(thenBranch)){
                        String variableName = extractVariableName(condition);

                        holder.registerProblem(condition.getFirstChild(), // Highlight
                                "LogSensei: Null check without WARN log"
                                , ProblemHighlightType.WEAK_WARNING,
                                new NullCheckLogQuickFix(variableName)
                        );
                    }
                }

            }
        };
    }

    private boolean isNullComparison(PsiExpression expression){
        if (!(expression instanceof PsiBinaryExpression)) {
            return false;
        }

        PsiBinaryExpression binary = (PsiBinaryExpression) expression;

        IElementType operator = binary.getOperationTokenType();

        if(operator != JavaTokenType.EQEQ && operator != JavaTokenType.NE) {
            return false;
        }

        PsiExpression left = binary.getLOperand();
        PsiExpression right = binary.getROperand();

        if(right == null){
            return false;
        }

        boolean leftIsNull = isNullLiteral(left);
        boolean rightIsNull = isNullLiteral(right);

        return (leftIsNull && !rightIsNull) || (!leftIsNull && rightIsNull);
    }

    private boolean isNullLiteral(PsiExpression expression) {
        if (!(expression instanceof PsiLiteralExpression)) {
            return false;
        }
            PsiLiteralExpression literal = (PsiLiteralExpression) expression;
            return literal.getValue() == null && "null".equals(literal.getText());
    }

    private boolean hasWarningLog(PsiStatement thanBranch){
        String text = thanBranch.getText();
        return text.contains("log.warn") || text.contains("logger.warn");
    }

    private String extractVariableName(PsiExpression condition){
        if (!(condition instanceof PsiBinaryExpression)) {
            return "variable";
        }

        PsiBinaryExpression binary = (PsiBinaryExpression) condition;
        PsiExpression left = binary.getLOperand();
        PsiExpression right = binary.getROperand();

        boolean leftIsNull = isNullLiteral(left);
        boolean rightIsNull = isNullLiteral(right);

        if (!leftIsNull) {
            return left.getText();
        } else if (right != null && !rightIsNull) {
            return right.getText();
        }
        return "variable";
    }
}
