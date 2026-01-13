package com.jakubbone.logsensei.quickfix;

import static com.jakubbone.logsensei.psi.LogImplementationService.implementLoggingSolution;
import static com.jakubbone.logsensei.psi.LogStatementFactory.createErrorLog;
import static com.jakubbone.logsensei.education.LogEducationNotifier.showErrorLevelEducation;
import static com.jakubbone.logsensei.dependency.ui.DependencyDialogService.askUserForLibraryAndAnnotation;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;
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
    public boolean startInWriteAction() {
        return false; //Manage write action manually to allow user dialogs
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        // Ask user BEFORE write action (dialogs cannot be shown during write action)
        LoggingLibrary lib = askUserForLibraryAndAnnotation(project);
        if(lib != null){
            // Now run the code modifications in a write action
            WriteCommandAction.runWriteCommandAction(project, () -> {
                PsiElement catchKeyword = descriptor.getPsiElement();
                addLog(project, catchKeyword, lib);
            });
            showErrorLevelEducation(project);
        }
    }

    public void addLog(Project project, PsiElement catchKeyword, LoggingLibrary selectedLibrary){

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


        PsiParameter exceptionParameter = catchSection.getParameter();
        if(exceptionParameter == null){
            return;
        }

        implementLoggingSolution(project, containingClass, selectedLibrary);

        PsiStatement logStmt = createErrorLog(
                project,
                containingMethod.getName(),
                exceptionParameter.getName(),
                catchSection);

        PsiCodeBlock codeBlock = catchSection.getCatchBlock();
        if(codeBlock == null){
            return;
        }

        PsiStatement [] statements = codeBlock.getStatements();
        if (statements.length > 0) {
            codeBlock.addBefore(logStmt, statements[0]);
        } else {
            codeBlock.add(logStmt);
        }
    }
}
