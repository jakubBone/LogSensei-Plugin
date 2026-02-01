package com.jakubbone.logsensei.inspection;

import static com.jakubbone.logsensei.inspection.detector.LogDetector.hasLogBeforeStatement;
import static com.jakubbone.logsensei.psi.LogImplementationService.detectExistingLoggerLibrary;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;
import com.jakubbone.logsensei.quickfix.EarlyReturnLogQuickFix;
import org.jetbrains.annotations.NotNull;

public class EarlyReturnInspection extends BaseLogInspection {

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

                if(isLastStatement(statement, body) || hasLogBeforeStatement(statement)){
                    return;
                }

                PsiClass containingClass = PsiTreeUtil.getParentOfType(statement, PsiClass.class);
                LoggingLibrary library = (containingClass != null)
                        ? detectExistingLoggerLibrary(containingClass)
                        : LoggingLibrary.NONE;

                registerLogProblem(holder,
                        statement.getFirstChild(),
                        "Early return missing log",
                        new EarlyReturnLogQuickFix(library)
                );
            }
        };
    }

    private boolean isLastStatement(PsiReturnStatement returnStmt, PsiCodeBlock methodBody){
        PsiStatement[] statements = methodBody.getStatements();
        return statements.length > 0 && returnStmt.equals(statements[statements.length - 1]);
    }
}
