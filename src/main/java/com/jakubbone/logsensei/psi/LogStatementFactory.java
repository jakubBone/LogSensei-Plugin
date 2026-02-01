package com.jakubbone.logsensei.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiStatement;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;
import org.jetbrains.annotations.NotNull;

public class LogStatementFactory {

    public static final String errorPattern = "%s.error(\"[{}] An exception occurred.\", \"%s\", e);";
    public static final String debugPattern = "%s.debug(\"[%s] Early return\");";
    public static final String infoEntryPattern = "%s.info(\"[%s] Operation started\");";
    public static final String exitInfoPattern = "%s.info(\"[%s] Operation finished\");";

    public static final String julErrorPattern = "%s.log(java.util.logging.Level.SEVERE, \"[%s] An exception occurred.\", %s);";
    public static final String julDebugPattern = "%s.log(java.util.logging.Level.FINE, \"[%s] Early return\");";
    public static final String julInfoEntryPattern = "%s.log(java.util.logging.Level.INFO, \"[%s] Operation started\");";
    public static final String julInfoExitPattern = "%s.log(java.util.logging.Level.INFO, \"[%s] Operation finished\");";

    public static PsiStatement createErrorLog(
            @NotNull Project project,
            @NotNull String loggerName,
            @NotNull String methodName,
            @NotNull String exceptionName,
            @NotNull PsiElement context,
            @NotNull LoggingLibrary library) {

        String pattern = (library == LoggingLibrary.JAVA_UTIL_LOGGING) ? julErrorPattern : errorPattern;
        String logText = String.format(pattern, loggerName, methodName, exceptionName);
        return createStatement(project, logText, context);
    }

    public static PsiStatement createDebugLog(
            @NotNull Project project,
            @NotNull String loggerName,
            @NotNull String methodName,
            @NotNull PsiElement context,
            @NotNull LoggingLibrary library) {

        String pattern = (library == LoggingLibrary.JAVA_UTIL_LOGGING) ? julDebugPattern : debugPattern;
        String logText = String.format(pattern, loggerName, methodName);
        return createStatement(project, logText, context);
    }

    public static PsiStatement createEntryLog(
            @NotNull Project project,
            @NotNull String loggerName,
            @NotNull String methodName,
            @NotNull PsiElement context,
            @NotNull LoggingLibrary library) {

        String pattern = (library == LoggingLibrary.JAVA_UTIL_LOGGING) ? julInfoEntryPattern : infoEntryPattern;
        String logText = String.format(pattern, loggerName, methodName);
        return createStatement(project, logText, context);
    }

    public static PsiStatement createExitLog(
            @NotNull Project project,
            @NotNull String loggerName,
            @NotNull String methodName,
            @NotNull PsiElement context,
            @NotNull LoggingLibrary library) {

        String pattern = (library == LoggingLibrary.JAVA_UTIL_LOGGING) ? julInfoExitPattern : exitInfoPattern;
        String logText = String.format(pattern, loggerName, methodName);
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
