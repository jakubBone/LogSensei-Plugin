package com.jakubbone.logsensei.inspection;

import static com.jakubbone.logsensei.inspection.detector.LogDetector.hasLogInBlock;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;

import com.jakubbone.logsensei.quickfix.CatchBlockLogQuickFix;
import org.jetbrains.annotations.NotNull;

public class CatchBlockInspection extends BaseLogInspection {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitCatchSection(@NotNull PsiCatchSection section) {
                super.visitCatchSection(section);

                PsiCodeBlock catchBlock = section.getCatchBlock();
                if (catchBlock == null){
                    return;
                }

                if (hasLogInBlock(catchBlock)) {
                    return;
                }

                registerLogProblem(
                        holder,
                        section.getFirstChild(),
                        "Catch block missing ERROR log",
                        new CatchBlockLogQuickFix()
                );
            }
        };
    }
}
