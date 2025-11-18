package com.jakubbone.logsensei.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiStatement;
import org.jetbrains.annotations.NotNull;

public class LogStatementFactory {

    public static final String errorPattern = "log.error(\"[{}] An exception occurred.\", \"%s\", e);";
    public static final String debugPattern = "log.debug(\"[%s] Early return\");";
    public static final String warnPattern = "log.warn(\"[%s] Variable '%s' is null\", \"%s\");";
    public static final String infoEntryPattern = "log.info(\"[%s] Operation started\");";
    public static final String exitInfoPattern = "log.info(\"[%s] Operation finished\");";

    public static PsiStatement createErrorLog(
            @NotNull Project project,
            @NotNull String methodName,
            @NotNull String exceptionName,
            @NotNull PsiElement context) {

        String logText = String.format(errorPattern, methodName, exceptionName);
        return createStatement(project, logText, context);
    }

    public static PsiStatement createDebugLog(
            @NotNull Project project,
            @NotNull String methodName,
            @NotNull PsiElement context) {

        String logText = String.format(debugPattern, methodName);
        return createStatement(project, logText, context);
    }

    public static PsiStatement createWarnLog(
            @NotNull Project project,
            @NotNull String methodName,
            @NotNull String variableName,
            @NotNull PsiElement context) {

        String logText = String.format(warnPattern, methodName, variableName, variableName);
        return createStatement(project, logText, context);
    }

    public static PsiStatement createEntryLog(
            @NotNull Project project,
            @NotNull String methodName,
            @NotNull PsiElement context) {

        String logText = String.format(infoEntryPattern, methodName);
        return createStatement(project, logText, context);
    }

    public static PsiStatement createExitLog(
            @NotNull Project project,
            @NotNull String methodName,
            @NotNull PsiElement context) {

        String logText = String.format(exitInfoPattern, methodName);
        return createStatement(project, logText, context);
    }

    private static PsiStatement createStatement(
            @NotNull Project project,
            @NotNull String statementText,
            @NotNull PsiElement context) {

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        return factory.createStatementFromText(statementText, context);
    }
}
