package com.jakubbone.logsensei;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents the Quick Fix action that will be available
 * when an empty catch block is detected.
 *
 * It will insert the log.error(...) snippet.
 */
public class AddErrorLogQuickFix implements LocalQuickFix {
    private static final String LOMBOK_LOG4J2_ANNOTATION = "lombok.extern.log4j.Log4j2";

    public @IntentionFamilyName @NotNull String getFamilyName() {
        return "Add error log";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        // 1. Get the element that was highlighted (the 'catch' keyword)
        PsiElement catchKeyword = descriptor.getPsiElement();

        // 2. Find the parent PsiCatchSection from the keyword
        PsiCatchSection catchSection = PsiTreeUtil.getParentOfType(catchKeyword, PsiCatchSection.class);
        if(catchSection == null){
            return;
        }

        // 3. Find the class containing catch section to add the annotation
        PsiClass containingClass = PsiTreeUtil.getParentOfType(catchSection, PsiClass.class);
        if (containingClass == null) {
            return;
        }

        // 4. Find the method catch section to log.error(" ");
        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(catchSection, PsiMethod.class);
        if (containingMethod == null) {
            return;
        }
        String methodName = containingMethod.getName();

                PsiParameter exceptionParameter = catchSection.getParameter();
        if(exceptionParameter == null){
            return;
        }
        String exceptionName = exceptionParameter.getName();

        // add Log4J to class
        addLog4jAnnotationAndImports(project, containingClass);

        // Create the log statement
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

        String logStatementText = String.format(
                "log.error(\"[%s] An exception occurred.\", %s);",
                methodName,
                exceptionName
        );

        PsiStatement logStatement = factory.createStatementFromText(logStatementText, catchSection);

        // Add the statement to the catch block
        PsiCodeBlock codeBlock = catchSection.getCatchBlock();
        codeBlock.add(logStatement);

    }

    private void addLog4jAnnotationAndImports(@NotNull Project project, @NotNull PsiClass psiClass){
        PsiModifierList modifierList = psiClass.getModifierList();
        if(modifierList == null || modifierList.hasAnnotation(LOMBOK_LOG4J2_ANNOTATION)){
            return;
        }

        // Add annotation
        PsiElementFactory factory =  JavaPsiFacade.getElementFactory(project);
        PsiAnnotation annotation = factory.createAnnotationFromText("@"+LOMBOK_LOG4J2_ANNOTATION, psiClass);
        modifierList.addBefore(annotation, modifierList.getFirstChild());

        // Add imports and shorten class names
        JavaCodeStyleManager.getInstance(project).shortenClassReferences(psiClass.getContainingFile());
    }
}
