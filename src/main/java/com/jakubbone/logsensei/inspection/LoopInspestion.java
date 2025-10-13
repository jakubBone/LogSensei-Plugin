package com.jakubbone.logsensei.inspection;

import java.util.ArrayList;
import java.util.List;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiForStatement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiStatement;
import org.jetbrains.annotations.NotNull;

public class LoopInspestion extends AbstractBaseJavaLocalInspectionTool {
    /*
    * PsiForStatement          // for(int i = 0; i < 10; i++)
      ├── PsiCodeBlock         // {}
          └── PsiStatement[]   // all instruction in the loop

      PsiWhileStatement        // while
      ├── PsiExpression        // condition
      └── PsiStatement         // body (block or single statement)

      PsiDoWhileStatement      // do {} while (condition)

      PsiForeachStatement      // for(String item : list)
      ├── PsiParameter         // item
      ├── PsiExpression        // list
      └── PsiStatement         // body
    */

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {

            @Override
            public void visitForStatement(@NotNull PsiForStatement statement) {
                super.visitForStatement(statement);
                checkLoopForHighFrequencyLogging(statement, holder);
            }
        };
    }

    private void checkLoopForHighFrequencyLogging(
            PsiForStatement statement,
            ProblemsHolder holder) {

        PsiStatement loopBody = statement.getBody();
        if (loopBody == null) {
            return;
        }

        List<PsiMethodCallExpression> problematicLogs = findProblematicLogCalls(statement);

        if (!problematicLogs.isEmpty()) {
            PsiElement loopKeyword = statement.getFirstChild();

            holder.registerProblem(loopKeyword,
                    "LogSensei: High-frequency logging detected in loop. Consider using DEBUG level.",
                    ProblemHighlightType.WEAK_WARNING
            );
        }
    }

    private List<PsiMethodCallExpression> findProblematicLogCalls(PsiStatement statement){
        List<PsiMethodCallExpression> problematicLogs = new ArrayList<>();

        statement.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(@NotNull PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);

                String methodCall = expression.getText();

                if (methodCall.startsWith("log.info") ||
                        methodCall.startsWith("log.warn") ||
                        methodCall.startsWith("log.error") ||
                        methodCall.startsWith("logger.info") ||
                        methodCall.startsWith("logger.warn")) {
                    problematicLogs.add(expression);
                }
            }
        });
        return problematicLogs;
    }
}


