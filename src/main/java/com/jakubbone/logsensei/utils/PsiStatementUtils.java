package com.jakubbone.logsensei.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiBlockStatement;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiStatement;

public class PsiStatementUtils {

    /**
     * Adds a log statement before the target statement.
     * If the target statement is not in a block (e.g., single-line if),
     * wraps both statements in a new block.
     *
     * Example transformations:
     * - Block context: { stmt1; stmt2; } → { stmt1; LOG; stmt2; }
     * - Single statement: if (x) return; → if (x) { LOG; return; }
     *
     * @param project the current project
     * @param logStmt the log statement to add
     * @param targetStmt the statement before which to add the log
     */
    public static void addLogBeforeStatement(Project project, PsiStatement logStmt, PsiStatement targetStmt){
        PsiElement parent = targetStmt.getParent();

        if(parent instanceof PsiCodeBlock){
            parent.addBefore(logStmt, targetStmt);
        } else {
            wrapInBLock(project, logStmt, targetStmt);
        }
    }

    /**
     * Wraps a single-line return statement in a block with the log statement.
     * Example: if (x) return; → if (x) { log.info(...); return; }
     */
    private static void wrapInBLock(Project project, PsiStatement logStmt, PsiStatement targetStmt){
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        String blockText = String.format(
                "{ %s %s }",
                logStmt.getText(),
                targetStmt.getText()
        );
        PsiBlockStatement newBlock = (PsiBlockStatement) factory.createStatementFromText(blockText, targetStmt);
        targetStmt.replace(newBlock);
    };
}
