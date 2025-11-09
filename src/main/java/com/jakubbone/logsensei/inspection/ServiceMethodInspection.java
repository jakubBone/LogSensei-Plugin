package com.jakubbone.logsensei.inspection;

import static com.jakubbone.logsensei.utils.LogDetectionUtils.containsLogCall;
import static com.jakubbone.logsensei.utils.LogSenseiConstants.SERVICE_ANNOTATION;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jakubbone.logsensei.quickfix.ServiceMethodLogQuickFix;
import org.jetbrains.annotations.NotNull;

public class ServiceMethodInspection extends BaseLogInspection {
    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitMethod(@NotNull PsiMethod method) {
                super.visitMethod(method);

                if (!isPublicServiceMethod(method)) {
                    return;
                }

                PsiCodeBlock body = method.getBody();
                if (body == null) {
                    return;
                }

                PsiIdentifier nameIdentifier = method.getNameIdentifier();
                if (nameIdentifier == null) {
                    return;
                }

                boolean hasEntry = hasLogAtPosition(body, 0);
                boolean hasExit = hasLogAtPosition(body, body.getStatements().length - 1);

                if (hasEntry && hasExit) {
                    return;
                }

                String description;
                if (!hasEntry && !hasExit) {
                    description = "LogSensei: Service method missing entry and exit logs";
                } else if (!hasEntry) {
                    description = "LogSensei: Service method missing entry log";
                } else {
                    description = "LogSensei: Service method missing exit log";
                }

                registerLogProblem(
                        holder,
                        nameIdentifier,
                        buildDescription(hasEntry, hasExit),
                        new ServiceMethodLogQuickFix(hasEntry, hasExit)
                );
            }
        };
    }

    private boolean isPublicServiceMethod(@NotNull PsiMethod method) {
        if (!method.hasModifierProperty("public")) {
            return false;
        }

        PsiClass containingClass = PsiTreeUtil.getParentOfType(method, PsiClass.class);
        if (containingClass == null) {
            return false;
        }

        PsiModifierList modifierList = containingClass.getModifierList();
        return modifierList != null && modifierList.hasAnnotation(SERVICE_ANNOTATION);
    }

    private boolean hasLogAtPosition(PsiCodeBlock body, int position) {
        PsiStatement[] statements = body.getStatements();
        if (position < 0 || position >= statements.length) {
            return false;
        }
        return containsLogCall(statements[position].getText());
    }

    private String buildDescription(boolean hasEntry, boolean hasExit) {
        if (!hasEntry && !hasExit) {
            return "Service method missing entry and exit logs";
        }
        return hasEntry ? "Service method missing exit log" : "Service method missing entry log";
    }
}
