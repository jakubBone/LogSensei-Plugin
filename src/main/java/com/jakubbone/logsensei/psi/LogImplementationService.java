package com.jakubbone.logsensei.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;
import org.jetbrains.annotations.NotNull;

public class LogImplementationService {
    private static final String JUL_LOGGER_FQN = "java.util.logging.Logger";

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

    private static void addJavaUtilLoggerField(@NotNull Project project,
                                               @NotNull PsiClass psiClass){

        if(psiClass.findFieldByName("logger", false) != null){
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
