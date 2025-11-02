package com.jakubbone.logsensei.inspection;

import static com.jakubbone.logsensei.utils.LogSenseiConstants.SERVICE_ANNOTATION;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.util.PsiTreeUtil;
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
                if(modifierList != null && !modifierList.hasAnnotation(SERVICE_ANNOTATION)){
                    return;
                }

                if(!method.hasModifierProperty("public")){
                    return;
                }

                PsiCodeBlock body = method.getBody();
                if(body == null){
                    return;
                }

                if(isFirstStatementLog(body)){

                }

                if (isLastStatementLog(body)){

                }

            }
        };
    }

    private boolean isFirstStatementLog(PsiCodeBlock methodBody){
        PsiStatement[] statements = methodBody.getStatements();
        if(statements.length == 0){
            return true;
        }
        PsiStatement firstStmt = statements[0];

        String text = firstStmt.getText();
        if(text.contains("log.") ||
                text.contains("logger.") ||
                text.contains("System.out.print")){
            return true;
        }
        return false;
    }

    private boolean isLastStatementLog(PsiCodeBlock methodBody){
        PsiStatement[] statements = methodBody.getStatements();
        if(statements.length == 0){
            return true;
        }
        PsiStatement lastStmt = statements[statements.length - 1];

        String text = lastStmt.getText();
        if(text.contains("log.") ||
                text.contains("logger.") ||
                text.contains("System.out.print")){
            return true;
        }
        return false;
    }
}
