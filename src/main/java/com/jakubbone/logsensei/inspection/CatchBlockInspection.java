package com.jakubbone.logsensei.inspection;

import static com.jakubbone.logsensei.utils.LogDetectionUtils.hasLogInBlock;

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
                if (catchBlock == null){
                    return;
                }

                if (hasLogInBlock(catchBlock)) {
                    return;
                }

                holder.registerProblem(section.getFirstChild(), // Highlight
                        "LogSensei: Catch block missing ERROR log",
                        ProblemHighlightType.WEAK_WARNING,
                        new CatchBlockLogQuickFix());
            }
        };
    }
}
