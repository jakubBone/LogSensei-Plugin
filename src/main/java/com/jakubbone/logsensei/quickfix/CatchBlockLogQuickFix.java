package com.jakubbone.logsensei.quickfix;

import static com.jakubbone.logsensei.utils.LogSenseiUtils.addAnnotationAndImports;
import static com.jakubbone.logsensei.utils.LogStatementFactory.createErrorLog;
import static com.jakubbone.logsensei.utils.LogEducationNotifier.showErrorLevelEducation;

import static com.jakubbone.logsensei.utils.LogSenseiUtils.addLog4jAnnotationAndImports;

import com.google.protobuf.Message;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.jakubbone.logsensei.dependency.DependencyManager;
import com.jakubbone.logsensei.dependency.model.BuildSystem;
import com.jakubbone.logsensei.dependency.model.DependencyDetector;
import com.jakubbone.logsensei.dependency.model.DependencyStatus;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents the Quick Fix action that will be available
 * when an empty catch block is detected.
 *
 * It will insert the log.error(...) snippet.
 */
public class CatchBlockLogQuickFix implements LocalQuickFix {

    public @IntentionFamilyName @NotNull String getFamilyName() {
        return "Add ERROR log";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        DependencyStatus status = DependencyDetector.detect(project);

        LoggingLibrary selectedLibrary;

        if(!status.hasLoggingLibrary()){
            selectedLibrary = askUserForAddLoggingLibrary(project, status);
            if(selectedLibrary == null){
                return;
            }
        } else {
            selectedLibrary = status.getDetectedLoggingLibrary();
        }


        if(!status.hasLombok()){
            askUserForAddLombok(project, status);
        }

        PsiElement catchKeyword = descriptor.getPsiElement();
        addLog(project, catchKeyword, selectedLibrary);

        showErrorLevelEducation(project);
    }

    private LoggingLibrary askUserForAddLoggingLibrary(Project project, DependencyStatus status) {
        BuildSystem buildSystem = status.getBuildSystem();

        if (buildSystem == BuildSystem.UNKNOWN) {
            Messages.showErrorDialog(
                    project,
                    "Cannot detect build system (Maven/Gradle).\nPlease add logging library manually.",
                    "Build System Not Detected"
            );
            return null;
        }

        int choice = Messages.showDialog(
                project,
                "No logging library detected\n\n",
                "LogSensei can add it automatically to your " + buildSystem.getToolName() + " project.\n\n" +
                        "Which logging library would you like to use?",
                new String[]{"Log4j2", "SLF4J + LOGBACK", "java.util.logging", "Cancel"},
                0,
                Messages.getQuestionIcon()
        );

        if (choice == 3) {
            return null;
        }

        LoggingLibrary lib = switch (choice) {
            case 1 -> LoggingLibrary.SLF4J_LOGBACK;
            case 2 -> LoggingLibrary.JAVA_UTIL_LOGGING;
            default -> LoggingLibrary.LOG4J2;
        };

        boolean success = DependencyManager.addDependencies(
                project,
                buildSystem,
                false, // No Lombok yet
                lib
        );

        if (success) {
            Messages.showInfoMessage(
                    project,
                    "Logging library added successfully!\n\n" +
                            "Please sync your project:\n" +
                            (buildSystem.isMaven() ? "Maven → Reload Project" : "Gradle → Sync Project"),
                    "Success"
            );
            return lib;
        } else {
            Messages.showErrorDialog(
                    project,
                    "Failed to add dependencies.\nPlease add them manually.",
                    "Error"
            );
            return null;
        }
    }

    private void askUserForAddLombok(Project project, DependencyStatus status) {
        int choice = Messages.showYesNoDialog(
                project,
                "Lombok not detected.\n\n" +
                        "Add Lombok for automatic logger generation?\n" +
                        "(You can use @Log4j2 annotation instead of manual logger field)",
                "Add Lombok?",
                Messages.getQuestionIcon()
        );

        if (choice == Messages.YES) {
            BuildSystem buildSystem = status.getBuildSystem();
            DependencyManager.addDependencies(
                    project,
                    buildSystem,
                    true, // Add Lombok
                    LoggingLibrary.NONE // Already have logging
            );

            Messages.showInfoMessage(
                    project,
                    "Lombok added! Don't forget to sync your project.",
                    "Success"
            );
        }
    }


    private void addLog(Project project, PsiElement catchKeyword, LoggingLibrary selectedLibrary){
        PsiCatchSection catchSection = PsiTreeUtil.getParentOfType(catchKeyword, PsiCatchSection.class);
        if(catchSection == null){
            return;
        }

        PsiClass containingClass = PsiTreeUtil.getParentOfType(catchSection, PsiClass.class);
        if (containingClass == null) {
            return;
        }

        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(catchSection, PsiMethod.class, false);
        if (containingMethod == null) {
            return;
        }


        PsiParameter exceptionParameter = catchSection.getParameter();
        if(exceptionParameter == null){
            return;
        }

        addAnnotationAndImports(project, containingClass, selectedLibrary);

        PsiStatement logStmt = createErrorLog(
                project,
                containingMethod.getName(),
                exceptionParameter.getName(),
                catchSection);

        PsiCodeBlock codeBlock = catchSection.getCatchBlock();
        if(codeBlock == null){
            return;
        }

        PsiStatement [] statements = codeBlock.getStatements();
        if (statements.length > 0) {
            codeBlock.addBefore(logStmt, statements[0]);
        } else {
            codeBlock.add(logStmt);
        }
    }
}
