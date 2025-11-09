package com.jakubbone.logsensei.utils;

import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiWhiteSpace;

public class LogDetectionUtils {

    public static boolean hasLogBeforeStatement(PsiElement statement){
        PsiElement previous = statement.getPrevSibling();

        while (previous instanceof PsiWhiteSpace || previous instanceof PsiComment) {
            previous = previous.getPrevSibling();
        }

        if (previous instanceof PsiStatement) {
            return hasLogStatement(previous.getText());
        }
        return false;
    }

    public static boolean hasLogInBlock(PsiCodeBlock block){
        PsiStatement[] statements = block.getStatements();

        if(statements.length == 0){
            return true;
        }

        for(PsiStatement stmt: statements){
            String text = stmt.getText();
            if(hasLogStatement(text)){
                return false;
            }
        }
        return true;
    }

    public static boolean hasLogStatement(String statementText) {
        return statementText.contains("log.error") ||
                statementText.contains("log.warn") ||
                statementText.contains("log.info") ||
                statementText.contains("log.debug") ||
                statementText.contains("logger.") ||
                statementText.contains("System.out.print") ||
                statementText.contains("printStackTrace");
    }
}

