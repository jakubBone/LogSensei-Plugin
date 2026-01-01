package com.jakubbone.logsensei.inspection;

import java.util.ArrayList;
import java.util.List;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiDoWhileStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiForStatement;
import com.intellij.psi.PsiForeachStatement;
import com.intellij.psi.PsiLoopStatement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiWhileStatement;
import com.jakubbone.logsensei.quickfix.LoopLogQuickFix;
import org.jetbrains.annotations.NotNull;

public class LoopInspection extends BaseLogInspection {

    private static final String[] INFO_LOG_PATTERNS = {"log.info", "logger.info"};

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {

            @Override
            public void visitForStatement(@NotNull PsiForStatement statement) {
                super.visitForStatement(statement);
                checkLoop(statement, holder);
            }

            @Override
            public void visitForeachStatement(@NotNull PsiForeachStatement statement) {
                super.visitForeachStatement(statement);
                checkLoop(statement, holder);
            }

            @Override
            public void visitWhileStatement(@NotNull PsiWhileStatement statement) {
                super.visitWhileStatement(statement);
                checkLoop(statement, holder);

            }

            @Override
            public void visitDoWhileStatement(@NotNull PsiDoWhileStatement statement) {
                super.visitDoWhileStatement(statement);
                checkLoop(statement, holder);
            }
        };
    }

    private void checkLoop(@NotNull PsiLoopStatement statement, @NotNull ProblemsHolder holder) {
        if (statement.getBody() == null) {
            return;
        }

        List<PsiMethodCallExpression> problematicLogs = findInfoLogsInLoop(statement);
        if (problematicLogs.isEmpty()) {
            return;
        }

        for(PsiMethodCallExpression call: problematicLogs){
            PsiElement methodCallElement = call.getMethodExpression().getReferenceNameElement();
            if (methodCallElement != null) {
                registerLogProblem(
                        holder,
                        methodCallElement, // info log
                        "High-frequency logs detected in loop. Consider using DEBUG level.",
                        new LoopLogQuickFix(problematicLogs)
                );
            }
        }

    }

    private List<PsiMethodCallExpression> findInfoLogsInLoop(PsiStatement statement){
        List<PsiMethodCallExpression> problematicLogs = new ArrayList<>();

        statement.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(@NotNull PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);

                String methodCall = expression.getText();
                for (String pattern : INFO_LOG_PATTERNS) {
                    if (methodCall.contains(pattern)) {
                        problematicLogs.add(expression);
                        break;
                    }
                }
            }
        });
        return problematicLogs;
    }
}
