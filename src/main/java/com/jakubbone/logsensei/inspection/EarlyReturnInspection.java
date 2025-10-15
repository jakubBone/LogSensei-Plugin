package com.jakubbone.logsensei.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class EarlyReturnInspection extends AbstractBaseJavaLocalInspectionTool {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor(){
            @Override
            public void visitReturnStatement(@NotNull PsiReturnStatement statement) {
                super.visitReturnStatement(statement);
                PsiExpression returnValue = statement.getReturnValue();

                if(returnValue != null){
                    return;
                }

                PsiMethod method = PsiTreeUtil.getParentOfType(statement, PsiMethod.class);
                if(method == null){
                    return;
                }

                PsiCodeBlock body = method.getBody();
                if(body == null){
                    return;
                }

                // isLastStatement
                // hasLogBeforeReturn()
                // isPrintBeforeMethod()

                // registerProblem()

            }
        };
    }
}
