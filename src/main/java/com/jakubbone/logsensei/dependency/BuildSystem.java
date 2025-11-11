package com.jakubbone.logsensei.dependency;

import lombok.Getter;

@Getter
public enum BuildSystem {
    MAVEN("Maven", "pom.xml"),
    GRADLE_GROOVE("Gradle (Groovy)", "build.gradle"),
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
        return this == GRADLE_GROOVE || this == GRADLE_KOTLIN;
    }
}
