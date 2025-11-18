package com.jakubbone.logsensei.quickfix;

import static com.jakubbone.logsensei.utils.LogSenseiUtils.implementLoggingSolution;
import static com.jakubbone.logsensei.utils.LogStatementFactory.createEntryLog;
import static com.jakubbone.logsensei.utils.LogEducationNotifier.showInfoLevelEducation;
import static com.jakubbone.logsensei.utils.PsiStatementUtils.addLogBeforeStatement;
import static com.jakubbone.logsensei.utils.UserInteractionService.askUserForLibraryAndAnnotation;

import java.util.Collection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;

import com.intellij.psi.PsiMethod;

import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;
import com.jakubbone.logsensei.utils.LogStatementFactory;
import org.jetbrains.annotations.NotNull;

public class ServiceMethodLogQuickFix implements LocalQuickFix {
    private final boolean needsEntryLog;
    private final boolean needsExitLog;

    public ServiceMethodLogQuickFix(boolean hasEntryLog, boolean hasExitLog) {
        this.needsEntryLog = !hasEntryLog;
        this.needsExitLog = !hasExitLog;
    }

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        if (needsEntryLog && needsExitLog) {
            return "Add entry and exit INFO logs";
        } else if (needsEntryLog) {
            return "Add entry INFO log";
        } else {
            return "Add exit INFO log";
        }
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {

        LoggingLibrary selectedLibrary = askUserForLibraryAndAnnotation(project);
        if(selectedLibrary == null){
            return;
        }

        PsiElement psiElement = problemDescriptor.getPsiElement();
        if(psiElement == null){
            return;
        }

        PsiClass containingClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
        if(containingClass == null){
            return;
        }

        PsiMethod psiMethod = PsiTreeUtil.getParentOfType(psiElement, PsiMethod.class);
        if(psiMethod == null){
            return;
        }

        implementLoggingSolution(project, containingClass, selectedLibrary);

        if(needsEntryLog){
            addEntryLog(project, psiMethod);
        }

        if(needsExitLog){
            addExitLog(project, psiMethod);
        }

        showInfoLevelEducation(project);
    }

    private void addEntryLog(Project project, PsiMethod method){
        PsiCodeBlock body = method.getBody();
        if(body == null){
            return;
        }

        PsiStatement logStmt = createEntryLog(project, method.getName(), method);

        PsiStatement [] statements = body.getStatements();

        if(statements.length > 0){
            body.addBefore(logStmt, statements[0]);
        } else {
            body.add(logStmt);
        }
    }

    private void addExitLog(Project project, PsiMethod method){
        PsiCodeBlock body = method.getBody();
        if (body == null) {
            return;
        }

        Collection<PsiReturnStatement> returns =
                PsiTreeUtil.findChildrenOfAnyType(method, PsiReturnStatement.class);

        if(returns.isEmpty()){
            addLogAtEnd(project, method);
            return;
        }

        for(PsiReturnStatement returnStmt: returns){
            if(!hasLogBeforeReturn(returnStmt)){
                PsiStatement logStmt = LogStatementFactory.createExitLog(
                        project,
                        method.getName(),
                        returnStmt
                );
                addLogBeforeStatement(project, logStmt, returnStmt);
            }
        }
    }

    private void addLogAtEnd(Project project, PsiMethod method){
        PsiStatement logStmt = LogStatementFactory.createExitLog(
                project,
                method.getName(),
                method
        );

        PsiCodeBlock block = method.getBody();
        if(block != null) {
            block.add(logStmt);
        }
    }

    private boolean hasLogBeforeReturn(PsiReturnStatement returnStmt){
        PsiElement previous = returnStmt.getPrevSibling();

        if((previous instanceof PsiWhiteSpace || previous instanceof PsiComment)){
            previous = previous.getPrevSibling();
        }

        if(previous instanceof PsiStatement){
            String text = previous.getText();
            return text.contains("log.") ||
                    text.contains("logger.") ||
                    text.contains("System.out.print");
        }
        return false;
    }
}
