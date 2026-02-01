package com.jakubbone.logsensei.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiType;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;
import org.jetbrains.annotations.NotNull;

import static com.jakubbone.logsensei.dependency.model.LoggingLibrary.*;

public class LogImplementationService {
    private static final String JUL_LOGGER_FQN = "java.util.logging.Logger";
    private static final String DEFAULT_LOGGER_NAME = "log";
    private static final String[] LOMBOK_LOG_ANNOTATIONS = {
            "lombok.extern.slf4j.Slf4j",
            "lombok.extern.log4j.Log4j2",
            "lombok.extern.log4j.Log4j",
            "lombok.extern.java.Log",
            "lombok.extern.slf4j.XSlf4j",
            "lombok.extern.apachecommons.CommonsLog",
            "lombok.extern.jbosslog.JBossLog",
            "lombok.extern.flogger.Flogger"
    };

    public static void implementLoggingSolution(@NotNull Project project,
                                                @NotNull PsiClass psiClass,
                                                @NotNull LoggingLibrary library){

        if (library.usesLombokAnnotation()) {
            addAnnotationAndImports(project, psiClass, library.getLombokAnnotationFqn());
        } else if (library == LoggingLibrary.JAVA_UTIL_LOGGING) {
            addJavaUtilLoggerField(project, psiClass);
        } else {
            // NONE
        }
    }

    public static void addAnnotationAndImports(@NotNull Project project,
                                                    @NotNull PsiClass psiClass,
                                                    @NotNull String annotationFqn) {

        PsiModifierList modifierList = psiClass.getModifierList();
        if (modifierList == null) {
            return;
        }

        if (modifierList.findAnnotation(annotationFqn) != null) {
            return;
        }

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

        PsiAnnotation annotation = factory.createAnnotationFromText(
                "@" + annotationFqn,
                psiClass
        );

        modifierList.addBefore(annotation, modifierList.getFirstChild());

        JavaCodeStyleManager.getInstance(project)
                .shortenClassReferences(psiClass.getContainingFile());
    }

    public static LoggingLibrary detectExistingLoggerLibrary(@NotNull PsiClass psiClass) {
        for (PsiField field : psiClass.getFields()) {
            String typeName = field.getType().getCanonicalText();
            if (typeName.equals("java.util.logging.Logger")) return JAVA_UTIL_LOGGING;
            if (typeName.equals("org.slf4j.Logger")) return SLF4J_LOGBACK;
            if (typeName.equals("org.apache.logging.log4j.Logger")) return LOG4J2;
        }

        PsiModifierList modifierList = psiClass.getModifierList();
        if (modifierList != null) {
            for (String lombokAnnotation : LOMBOK_LOG_ANNOTATIONS) {
                if (modifierList.findAnnotation(lombokAnnotation) != null) {
                    if (lombokAnnotation.equals("lombok.extern.java.Log")) return JAVA_UTIL_LOGGING;
                    if (lombokAnnotation.equals("lombok.extern.log4j.Log4j") ||
                            lombokAnnotation.equals("lombok.extern.log4j.Log4j2")) return LOG4J2;
                    return SLF4J_LOGBACK;
                }
            }
        }

        return NONE;
    }

    public static @NotNull String resolveLoggerFieldName(@NotNull PsiClass psiClass) {
        for (PsiField field : psiClass.getFields()) {
            PsiType type = field.getType();
            String typeName = type.getCanonicalText();
            if (typeName.contains("Logger") || typeName.contains("Log")) {
                return field.getName();
            }
        }

        PsiModifierList modifierList = psiClass.getModifierList();
        if (modifierList != null) {
            for (String lombokAnnotation : LOMBOK_LOG_ANNOTATIONS) {
                if (modifierList.findAnnotation(lombokAnnotation) != null) {
                    return DEFAULT_LOGGER_NAME;
                }
            }
        }

        return DEFAULT_LOGGER_NAME;
    }

    private static boolean hasLoggerField(@NotNull PsiClass psiClass) {
        for (PsiField field : psiClass.getFields()) {
            PsiType type = field.getType();
            String typeName = type.getCanonicalText();
            if (typeName.contains("Logger") || typeName.contains("Log")) {
                return true;
            }
        }
        return false;
    }

    private static void addJavaUtilLoggerField(@NotNull Project project,
                                               @NotNull PsiClass psiClass){

        if(hasLoggerField(psiClass)){
            return;
        }

        String className = psiClass.getName();
        if(className == null){
            return;
        }

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

        String loggerFieldText = String.format(
                "private static final %s log = %s.getLogger(%s.class.getName());",
                JUL_LOGGER_FQN,
                JUL_LOGGER_FQN,
                className
        );

        PsiField loggerField = factory.createFieldFromText(loggerFieldText, psiClass);

        PsiField[] existingFields = psiClass.getFields();
        if (existingFields.length > 0) {
            psiClass.addBefore(loggerField, existingFields[0]);
        } else {
            PsiElement lBrace = psiClass.getLBrace();
            if (lBrace != null) {
                psiClass.addAfter(loggerField, lBrace);
            }
        }

        JavaCodeStyleManager.getInstance(project).
                shortenClassReferences(psiClass.getContainingFile());
    }

}
