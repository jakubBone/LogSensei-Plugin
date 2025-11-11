package com.jakubbone.logsensei.dependency.model;

import lombok.Getter;

@Getter
public enum BuildSystem {
    MAVEN("Maven", "pom.xml"),
    GRADLE_GROOVY("Gradle (Groovy)", "build.gradle"),
    GRADLE_KOTLIN("Gradle (Kotlin)", "build.gradle.kts"),
    UNKNOWN("Unknown", null);

    private final String toolName;
    private final String buildFile;

    BuildSystem(String toolName, String buildFile) {
        this.toolName = toolName;
        this.buildFile = buildFile;
    }

    public boolean isMaven(){
        return this == MAVEN;
    }

    public boolean isGradle(){
        return this == GRADLE_GROOVY || this == GRADLE_KOTLIN;
    }
}
