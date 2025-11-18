package com.jakubbone.logsensei.quickfix;

import static com.jakubbone.logsensei.utils.LogSenseiUtils.implementLoggingSolution;
import static com.jakubbone.logsensei.utils.LogStatementFactory.createDebugLog;
import static com.jakubbone.logsensei.utils.LogEducationNotifier.showDebugLevelEducation;
import static com.jakubbone.logsensei.utils.PsiStatementUtils.addLogBeforeStatement;
import static com.jakubbone.logsensei.utils.UserInteractionService.askUserForLibraryAndAnnotation;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;
import org.jetbrains.annotations.NotNull;


public class EarlyReturnLogQuickFix implements LocalQuickFix {

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return "Add DEBUG log before early return";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {

        LoggingLibrary selectedLibrary = askUserForLibraryAndAnnotation(project);
        if(selectedLibrary == null){
            return;
        }

        PsiElement returnKeyword = problemDescriptor.getPsiElement();
        PsiReturnStatement returnStmt = PsiTreeUtil.getParentOfType(returnKeyword, PsiReturnStatement.class);
        if (returnStmt == null) {
            return;
        }

        PsiClass containingClass = PsiTreeUtil.getParentOfType(returnKeyword, PsiClass.class);
        if(containingClass == null){
            return;
        }

        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(returnKeyword, PsiMethod.class);
        if(containingMethod == null){
            return;
        }

        implementLoggingSolution(project, containingClass, selectedLibrary);

        PsiStatement logStmt = createDebugLog(
                project,
                containingClass.getName(),
                returnStmt
        );

        addLogBeforeStatement(project, logStmt, returnStmt);

        showDebugLevelEducation(project);
    }
}
