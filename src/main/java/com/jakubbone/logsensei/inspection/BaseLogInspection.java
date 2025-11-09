package com.jakubbone.logsensei.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public abstract class BaseLogInspection extends AbstractBaseJavaLocalInspectionTool {
    protected void registerLogProblem(
            @NotNull ProblemsHolder holder,
            @NotNull PsiElement element,
            @NotNull String message,
            @NotNull LocalQuickFix quickFix) {

        holder.registerProblem(
                element,
                "LogSensei: " + message,
                ProblemHighlightType.WEAK_WARNING,
                quickFix
        );
    }
}
