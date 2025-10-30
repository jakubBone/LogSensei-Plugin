package com.jakubbone.logsensei.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.jetbrains.annotations.NotNull;

public class LogSenseiUtils {
    private static final String LOMBOK_LOG4J2 = "lombok.extern.log4j.Log4j2";

    public static void addLog4jAnnotationAndImports(@NotNull Project project, @NotNull PsiClass psiClass){
        PsiModifierList modifierList = psiClass.getModifierList();
        if(modifierList == null || modifierList.hasAnnotation(LOMBOK_LOG4J2)){
            return;
        }

        // Add Lombok annotation
        PsiElementFactory factory =  JavaPsiFacade.getElementFactory(project);
        PsiAnnotation annotation = factory.createAnnotationFromText("@"+LOMBOK_LOG4J2, psiClass);
        modifierList.addBefore(annotation, modifierList.getFirstChild());

        // Add imports and shorten class names
        JavaCodeStyleManager.getInstance(project).shortenClassReferences(psiClass.getContainingFile());
    }
}
