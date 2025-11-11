package com.jakubbone.logsensei.dependency.model;

import static com.jakubbone.logsensei.dependency.BuildSystemDetector.detectBuildSystem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.vfs.VirtualFile;

public class DependencyDetector {

    public static DependencyStatus detectDependency(Project project){
        BuildSystem buildSystem = detectBuildSystem(project);

        boolean hasLombok = false;
        LoggingLibrary loggingLibrary = LoggingLibrary.NONE;

        if(buildSystem.isMaven() || buildSystem.isGradle()){
            VirtualFile buildFile = project.getProjectFile().findChild(buildSystem.getBuildFile());
            if(buildFile != null){
                String content = readFileContent(buildFile);
                hasLombok = content.contains("lombok");
                loggingLibrary = detectLoggingLibraryFromBuildFile(content);
            }
        }

        if(!hasLombok || loggingLibrary == LoggingLibrary.NONE) {
            Module [] modules = ModuleManager.getInstance(project).getModules();
            for(Module module: modules){
                OrderEntry[] entries = ModuleRootManager.getInstance(module).getOrderEntries();
                for(OrderEntry entry: entries){
                    if (entry instanceof LibraryOrderEntry) {
                        String libraryName = ((LibraryOrderEntry) entry).getLibraryName();
                        if (libraryName != null) {
                            libraryName = libraryName.toLowerCase();

                            if (!hasLombok && libraryName.contains("lombok")) {
                                hasLombok = true;
                            }

                            if (loggingLibrary == LoggingLibrary.NONE) {
                                if (libraryName.contains("log4j")) {
                                    loggingLibrary = LoggingLibrary.LOG4J2;
                                } else if (libraryName.contains("slf4j") || libraryName.contains("logback")) {
                                    loggingLibrary = LoggingLibrary.LOG4J2;
                                }
                            }
                        }
                    }
                }
            }
        }
        return new DependencyStatus(hasLombok, loggingLibrary, buildSystem);
    }

    private static LoggingLibrary detectLoggingLibraryFromBuildFile(String content){
        if(content.contains("log4j-core")){
            return LoggingLibrary.LOG4J2;
        }
        if(content.contains("slf4j") || content.contains("logback")){
            return LoggingLibrary.LOGBACK;
        }
        return LoggingLibrary.NONE;
    }

    private static String readFileContent(VirtualFile file) {
        try {
            return new String(file.contentsToByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
}
