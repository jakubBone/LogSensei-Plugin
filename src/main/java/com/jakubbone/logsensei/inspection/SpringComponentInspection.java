package com.jakubbone.logsensei.inspection;

import static com.jakubbone.logsensei.inspection.detector.LogDetector.containsLogCall;

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
import com.jakubbone.logsensei.quickfix.EntryExitLogQuickFix;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class SpringComponentInspection extends BaseLogInspection {

    /**
     * Annotation -> Display name mapping.
     * To add new annotation support, simply add a new entry here.
     */
    private static final Map<String, String> ANNOTATION_DISPLAY_NAMES = new LinkedHashMap<>();

    static {
        ANNOTATION_DISPLAY_NAMES.put("org.springframework.stereotype.Service", "Service");
        ANNOTATION_DISPLAY_NAMES.put("org.springframework.stereotype.Controller", "Controller");
        ANNOTATION_DISPLAY_NAMES.put("org.springframework.web.bind.annotation.RestController", "Controller");

    }

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitMethod(@NotNull PsiMethod method) {
                super.visitMethod(method);

                String componentType = getSpringComponentType(method);
                if (componentType == null) {
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

                registerLogProblem(
                        holder,
                        nameIdentifier,
                        buildDescription(componentType, hasEntry, hasExit),
                        new EntryExitLogQuickFix(hasEntry, hasExit)
                );
            }
        };
    }

    private String getSpringComponentType(@NotNull PsiMethod method) {
        if (!method.hasModifierProperty("public")) {
            return null;
        }

        PsiClass containingClass = PsiTreeUtil.getParentOfType(method, PsiClass.class);
        if (containingClass == null) {
            return null;
        }

        PsiModifierList modifierList = containingClass.getModifierList();
        if (modifierList == null) {
            return null;
        }

        return ANNOTATION_DISPLAY_NAMES.entrySet().stream()
                .filter(entry -> modifierList.hasAnnotation(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private boolean hasLogAtPosition(PsiCodeBlock body, int position) {
        PsiStatement[] statements = body.getStatements();
        if (position < 0 || position >= statements.length) {
            return false;
        }
        return containsLogCall(statements[position].getText());
    }

    private String buildDescription(String componentType, boolean hasEntry, boolean hasExit) {
        if (!hasEntry && !hasExit) {
            return componentType + " method missing entry and exit logs";
        }
        return hasEntry
                ? componentType + " method missing exit log"
                : componentType + " method missing entry log";
    }
}
