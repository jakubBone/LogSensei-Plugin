package com.jakubbone.logsensei.dependency;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jakubbone.logsensei.dependency.model.BuildSystem;

public class BuildSystemDetector {

    public static BuildSystem detectBuildSystem(Project project){
        VirtualFile baseDir = project.getBaseDir();
        if(baseDir == null){
            return BuildSystem.UNKNOWN;
        }

        VirtualFile pomXml = baseDir.findChild("pom.xml");
        if(pomXml != null && pomXml.exists()){
            return BuildSystem.MAVEN;
        }

        VirtualFile buildGradle = baseDir.findChild("build.gradle");
        if(buildGradle != null && buildGradle.exists()){
            return BuildSystem.GRADLE_GROOVY;
        }


        VirtualFile buildGradleKts = baseDir.findChild("build.gradle.kts");
        if(buildGradleKts != null && buildGradleKts.exists()){
            return BuildSystem.GRADLE_KOTLIN;
        }

        return BuildSystem.UNKNOWN;
    }

}
