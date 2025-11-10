package com.jakubbone.logsensei.quickfix;

import static com.jakubbone.logsensei.utils.LogEducationNotifier.showErrorLevelEducation;
import static com.jakubbone.logsensei.utils.LogSenseiConstants.LOG_PATTERN_ERROR;
import static com.jakubbone.logsensei.utils.LogSenseiUtils.addLog4jAnnotationAndImports;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents the Quick Fix action that will be available
 * when an empty catch block is detected.
 *
 * It will insert the log.error(...) snippet.
 */
public class CatchBlockLogQuickFix implements LocalQuickFix {

    public @IntentionFamilyName @NotNull String getFamilyName() {
        return "Add ERROR log";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiElement catchKeyword = descriptor.getPsiElement();

        PsiCatchSection catchSection = PsiTreeUtil.getParentOfType(catchKeyword, PsiCatchSection.class);
        if(catchSection == null){
            return;
        }

        PsiClass containingClass = PsiTreeUtil.getParentOfType(catchSection, PsiClass.class);
        if (containingClass == null) {
            return;
        }

        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(catchSection, PsiMethod.class, false);
        if (containingMethod == null) {
            return;
        }

        String methodName = containingMethod.getName();

        PsiParameter exceptionParameter = catchSection.getParameter();
        if(exceptionParameter == null){
            return;
        }
        String exceptionName = exceptionParameter.getName();

        addLog4jAnnotationAndImports(project, containingClass);

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

        String logStatementText = String.format(
                LOG_PATTERN_ERROR,
                methodName,
                exceptionName
        );

        PsiStatement logStatement = factory.createStatementFromText(logStatementText, catchSection);

        PsiCodeBlock codeBlock = catchSection.getCatchBlock();
        codeBlock.add(logStatement);

        showErrorLevelEducation(project);
    }
}
