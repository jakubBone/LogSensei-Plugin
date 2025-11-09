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

public class ServiceMethodInspection extends AbstractBaseJavaLocalInspectionTool {
    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitMethod(@NotNull PsiMethod method) {
                super.visitMethod(method);

                PsiClass containingClass = PsiTreeUtil.getParentOfType(method, PsiClass.class);
                if (containingClass == null) {
                    return;
                }

                PsiModifierList modifierList = containingClass.getModifierList();
                if (modifierList == null && !modifierList.hasAnnotation(SERVICE_ANNOTATION)) {
                    return;
                }

                if (!method.hasModifierProperty("public")) {
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

                boolean hasEntry = hasEntryLog(body);
                boolean hasExit = hasExitLog(body);
                if (hasEntry && hasExit){
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

                holder.registerProblem(nameIdentifier,
                        description,
                        ProblemHighlightType.WEAK_WARNING,
                        new ServiceMethodLogQuickFix(hasEntry, hasExit));
            }
        };
    }

    private boolean hasEntryLog(PsiCodeBlock methodBody){
        PsiStatement[] statements = methodBody.getStatements();
        if(statements.length == 0){
            return false;
        }
        PsiStatement firstStmt = statements[0];

        String text = firstStmt.getText();
        if(containsLogCall(text)){
            return true;
        }
        return false;
    }

    private boolean hasExitLog(PsiCodeBlock methodBody){
        PsiStatement[] statements = methodBody.getStatements();
        if(statements.length == 0){
            return false;
        }
        PsiStatement lastStmt = statements[statements.length - 1];

        String text = lastStmt.getText();
        if(containsLogCall(text)){
            return true;
        }
        return false;
    }
}
