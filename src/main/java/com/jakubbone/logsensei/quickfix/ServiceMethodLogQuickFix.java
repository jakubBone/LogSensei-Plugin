package com.jakubbone.logsensei.quickfix;

import static com.jakubbone.logsensei.utils.LogSenseiConstants.LOG_PATTERN_SERVICE_ENTRY_INFO;
import static com.jakubbone.logsensei.utils.LogSenseiConstants.LOG_PATTERN_SERVICE_EXIT_INFO;
import static com.jakubbone.logsensei.utils.LogSenseiUtils.addLog4jAnnotationAndImports;

import java.util.Collection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiBlockStatement;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;

import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethod;

import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
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

        if(needsEntryLog){
            addEntryLog(project, psiMethod);
        }

        if(needsExitLog){
            addExitLog(project, psiMethod);
        }

        addLog4jAnnotationAndImports(project, containingClass);

    }

    private void addEntryLog(Project project, PsiMethod method){
        PsiCodeBlock body = method.getBody();
        if(body == null){
            return;
        }

        // Create log statement
        String logStatementText = String.format(
                LOG_PATTERN_SERVICE_ENTRY_INFO,
                method.getName()
        );

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        PsiStatement logStatement = factory.createStatementFromText(logStatementText, method);

        PsiStatement [] statements = body.getStatements();

        if(statements.length > 0){
            body.addBefore(logStatement, statements[0]);
        } else {
            body.add(logStatement);
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
                String logStatementText = String.format(
                        LOG_PATTERN_SERVICE_EXIT_INFO,
                        method.getName()
                );
                addLogBeforeReturn(project, logStatementText, returnStmt);

            }
        }
    }

    private void addLogAtEnd(Project project, PsiMethod method){
        // Create log statement
        String logStatementText = String.format(
                LOG_PATTERN_SERVICE_EXIT_INFO,
                method.getName()
        );

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        PsiStatement logStatement = factory.createStatementFromText(logStatementText, method);

        PsiCodeBlock block = method.getBody();
        if(block != null) {
            block.add(logStatement);
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

    private void addLogBeforeReturn(Project project, String logStatementText, PsiReturnStatement returnStmt){
        PsiElementFactory factory =  JavaPsiFacade.getElementFactory(project);
        PsiStatement logStatement = factory.createStatementFromText(logStatementText, returnStmt);

        PsiElement parent = returnStmt.getParent();

        if(parent instanceof PsiCodeBlock){
            parent.add(logStatement);
        } else {
            wrapInBLock(project, logStatement, returnStmt);
        }

    }

    private void wrapInBLock(Project project, PsiStatement logStmt, PsiReturnStatement returnStmt){
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        String blockText = String.format(
                "{ %s %s }",
                logStmt.getText(),
                returnStmt.getText()
        );

        PsiBlockStatement newBlock = (PsiBlockStatement) factory.createStatementFromText(blockText, returnStmt);

        returnStmt.replace(newBlock);
    };
}
