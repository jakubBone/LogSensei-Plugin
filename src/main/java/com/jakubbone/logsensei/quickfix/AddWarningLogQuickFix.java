package com.jakubbone.logsensei.quickfix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class AddWarningLogQuickFix implements LocalQuickFix {
    private final String variableName;

    public AddWarningLogQuickFix(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return "";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {

    }
}
