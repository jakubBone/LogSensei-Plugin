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

                return;

            }
        };
    }

}
