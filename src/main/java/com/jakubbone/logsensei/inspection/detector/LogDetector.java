package com.jakubbone.logsensei.inspection.detector;

import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiWhiteSpace;

public class LogDetector {

    public static boolean hasLogBeforeStatement(PsiElement statement){
        PsiElement previous = statement.getPrevSibling();
        while (previous instanceof PsiWhiteSpace || previous instanceof PsiComment) {
            previous = previous.getPrevSibling();
        }

        if (previous instanceof PsiStatement) {
            return containsLogCall(previous.getText());
        }
        return false;
    }

    public static boolean hasLogInBlock(PsiCodeBlock block){
        PsiStatement[] statements = block.getStatements();
        for(PsiStatement stmt: statements){
            String text = stmt.getText();
            if(containsLogCall(text)){
                return true;
            }
        }
        return false;
    }

    public static boolean containsLogCall(String statementText) {
        String lowerText = statementText.toLowerCase();
        return lowerText.contains("log.error") ||
                lowerText.contains("log.warn") ||
                lowerText.contains("log.info") ||
                lowerText.contains("log.debug") ||
                lowerText.contains("log.trace") ||
                lowerText.contains("log.severe") ||
                lowerText.contains("log.warning") ||
                lowerText.contains("log.fine") ||
                lowerText.contains("log.finer") ||
                lowerText.contains("log.finest") ||
                lowerText.contains("log.config") ||
                lowerText.contains("logger.") ||
                lowerText.contains("system.out.print");
    }
}

