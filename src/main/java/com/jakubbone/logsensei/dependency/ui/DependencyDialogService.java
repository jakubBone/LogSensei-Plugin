package com.jakubbone.logsensei.dependency.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.jakubbone.logsensei.dependency.DependencyManager;
import com.jakubbone.logsensei.dependency.model.BuildSystem;
import com.jakubbone.logsensei.dependency.model.DependencyDetector;
import com.jakubbone.logsensei.dependency.model.DependencyStatus;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;
import com.jakubbone.logsensei.psi.LogImplementationService;
import org.jetbrains.annotations.Nullable;

public class DependencyDialogService {

    public static LoggingLibrary askUserForLibraryAndAnnotation(Project project, @Nullable PsiClass psiClass){
        if (psiClass != null) {
            LoggingLibrary existing = LogImplementationService.detectExistingLoggerLibrary(psiClass);
            if (existing != LoggingLibrary.NONE) {
                return existing;
            }
        }
        return askUserForLibraryAndAnnotation(project);
    }

    public static LoggingLibrary askUserForLibraryAndAnnotation(Project project){
        DependencyStatus status = DependencyDetector.detect(project);

        LoggingLibrary selectedLibrary;

        if(!status.hasLoggingLibrary()){
            selectedLibrary = askUserForAddLoggingLibrary(project, status);
            if(selectedLibrary == null){
                return null;
            }
        } else {
            selectedLibrary = status.getDetectedLoggingLibrary();
        }

        if(selectedLibrary.usesLombokAnnotation() && !status.hasLombok()){
            askUserForAddLombok(project, status);
        }
        return selectedLibrary;
    }

    public static LoggingLibrary askUserForAddLoggingLibrary(Project project, DependencyStatus status) {
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

    public static void askUserForAddLombok(Project project, DependencyStatus status) {
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
}
