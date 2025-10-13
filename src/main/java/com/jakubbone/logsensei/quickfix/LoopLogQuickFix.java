package com.jakubbone.logsensei.quickfix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class LoopLogQuickFix implements LocalQuickFix  {
    private static final String LOMBOK_LOG4J2_ANNOTATION = "lombok.extern.log4j.Log4j2";

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return "Add debug log";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
        PsiElement loopElement = problemDescriptor.getPsiElement();


    }
}
