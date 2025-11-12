package com.jakubbone.logsensei.dependency;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jakubbone.logsensei.dependency.model.BuildSystem;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;

public class DependencyManager {

    public static boolean addDependencies(Project project, BuildSystem buildSystem,
                                          boolean addLombok, LoggingLibrary loggingLibrary) {
        VirtualFile buildFile = project.getBaseDir().findChild(buildSystem.getBuildFile());
        if (buildFile == null) {
            return false;
        }
    }


    private static void addGradleDependencies(String content, boolean addLombok, LoggingLibrary loggingLibrary, boolean b){

    }

    private static void addMavenDependencies(String content, boolean addLombok, LoggingLibrary loggingLibrary){

    }


}
