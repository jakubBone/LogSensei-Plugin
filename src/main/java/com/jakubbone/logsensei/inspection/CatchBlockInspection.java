package com.jakubbone.logsensei.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;

import com.jakubbone.logsensei.quickfix.CatchBlockLogQuickFix;
import org.jetbrains.annotations.NotNull;

public class CatchBlockInspection extends AbstractBaseJavaLocalInspectionTool {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitCatchSection(@NotNull PsiCatchSection section) {
                super.visitCatchSection(section);

                PsiCodeBlock catchBlock = section.getCatchBlock();
                if(catchBlock == null){
                    return;
                }

                if(isWithoutLogger(catchBlock)){
                    holder.registerProblem(section.getFirstChild(), // Highlight
                            "LogSensei: Catch block without error logging"
                    ,ProblemHighlightType.WEAK_WARNING,
                            new CatchBlockLogQuickFix());
                }
            }

            public boolean isWithoutLogger(PsiCodeBlock block){
                PsiStatement[] statements = block.getStatements();

                if(statements.length == 0){
                    return true;
                }

                for(PsiStatement stmt: statements){
                    String text = stmt.getText();
                    if(text.contains("log.error")){
                        return false;
                    }
                }
                return true;
            }
        };
    }
}
