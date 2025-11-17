package com.jakubbone.logsensei.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;
import org.jetbrains.annotations.NotNull;

public class LogSenseiUtils {
    private static final String LOMBOK_LOG4J2_FQN = "lombok.extern.log4j.Log4j2";
    private static final String LOMBOK_SLF4J_FQN = "lombok.extern.slf4j.Slf4j";

    public static void addAnnotationAndImports(@NotNull Project project,
                                                    @NotNull PsiClass psiClass,
                                                    @NotNull LoggingLibrary library){
        PsiModifierList modifierList = psiClass.getModifierList();
        if(modifierList == null){
            return;
        }

        if (library == LoggingLibrary.JAVA_UTIL_LOGGING) {
            return;
        }

        String annotationName = getAnnotationName(library);
        if (annotationName == null) {
            return;
        }

        if (modifierList.findAnnotation(annotationName) != null) {
            return;
        }

        PsiElementFactory factory =  JavaPsiFacade.getElementFactory(project);

        PsiAnnotation annotation = factory.createAnnotationFromText(
                "@" + annotationName,
                psiClass
        );

        modifierList.addBefore(annotation, modifierList.getFirstChild());

        JavaCodeStyleManager.getInstance(project)
                .shortenClassReferences(annotation);

        // TODO: addJavaApiLoggerField if no outside libs
        // TODO: make addLog4jAnnotationAndImports abstract for rest of libs
        // TODO: shorten  @lombok.extern.log4j.Log4j2, to @Log4j2
    }

    private static String getAnnotationName(LoggingLibrary library) {
        return switch (library) {
            case LOG4J2 -> LOMBOK_LOG4J2_FQN;
            case SLF4J_LOGBACK -> LOMBOK_SLF4J_FQN;
            default -> null; // Not used
        };
    }

    public static void addLog4jAnnotationAndImports(@NotNull Project project,
                                               @NotNull PsiClass psiClass){

        PsiModifierList modifierList = psiClass.getModifierList();
        if(modifierList == null){
            return;
        }

        if (modifierList.findAnnotation(LOMBOK_LOG4J2_FQN) != null) {
            return;
        }

        PsiElementFactory factory =  JavaPsiFacade.getElementFactory(project);

        PsiAnnotation annotation = factory.createAnnotationFromText(
                "@" + LOMBOK_LOG4J2_FQN,
                psiClass
        );

        modifierList.addBefore(annotation, modifierList.getFirstChild());

        JavaCodeStyleManager.getInstance(project)
                .shortenClassReferences(annotation);

        // TODO: addJavaApiLoggerField if no outside libs
        // TODO: make addLog4jAnnotationAndImports abstract for rest of libs
        // TODO: shorten  @lombok.extern.log4j.Log4j2, to @Log4j2
    }
}
