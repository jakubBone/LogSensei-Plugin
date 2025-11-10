package com.jakubbone.logsensei.utils;

import static com.jakubbone.logsensei.utils.LogSenseiConstants.LOG_PATTERN_DEBUG;
import static com.jakubbone.logsensei.utils.LogSenseiConstants.LOG_PATTERN_ERROR;
import static com.jakubbone.logsensei.utils.LogSenseiConstants.LOG_PATTERN_SERVICE_ENTRY_INFO;
import static com.jakubbone.logsensei.utils.LogSenseiConstants.LOG_PATTERN_SERVICE_EXIT_INFO;
import static com.jakubbone.logsensei.utils.LogSenseiConstants.LOG_PATTERN_WARN;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiStatement;
import org.jetbrains.annotations.NotNull;

public class LogStatementFactory {

    public static PsiStatement createErrorLog(
            @NotNull Project project,
            @NotNull String methodName,
            @NotNull String exceptionName,
            @NotNull PsiElement context) {

        String logText = String.format(LOG_PATTERN_ERROR, methodName, exceptionName);
        return createStatement(project, logText, context);
    }

    public static PsiStatement createDebugLog(
            @NotNull Project project,
            @NotNull String methodName,
            @NotNull PsiElement context) {

        String logText = String.format(LOG_PATTERN_DEBUG, methodName);
        return createStatement(project, logText, context);
    }

    public static PsiStatement createWarnLog(
            @NotNull Project project,
            @NotNull String methodName,
            @NotNull String variableName,
            @NotNull PsiElement context) {

        String logText = String.format(LOG_PATTERN_WARN, methodName, variableName, variableName);
        return createStatement(project, logText, context);
    }

    public static PsiStatement createEntryLog(
            @NotNull Project project,
            @NotNull String methodName,
            @NotNull PsiElement context) {

        String logText = String.format(LOG_PATTERN_SERVICE_ENTRY_INFO, methodName);
        return createStatement(project, logText, context);
    }

    public static PsiStatement createExitLog(
            @NotNull Project project,
            @NotNull String methodName,
            @NotNull PsiElement context) {

        String logText = String.format(LOG_PATTERN_SERVICE_EXIT_INFO, methodName);
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
