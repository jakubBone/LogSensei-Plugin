package com.jakubbone.logsensei.dependency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.jakubbone.logsensei.dependency.model.BuildSystem;
import org.junit.Test;

public class BuildSystemTest {
    @Test
    public void isMaven_shouldReturnTrueOnlyForMaven() {
        assertTrue("MAVEN should be Maven",
                BuildSystem.MAVEN.isMaven());
        assertFalse("GRADLE_GROOVY should not be Maven",
                BuildSystem.GRADLE_GROOVY.isMaven());
        assertFalse("GRADLE_KOTLIN should not be Maven",
                BuildSystem.GRADLE_KOTLIN.isMaven());
        assertFalse("UNKNOWN should not be Maven",
                BuildSystem.UNKNOWN.isMaven());
    }

    @Test
    public void isGradle_shouldReturnTrueForBothVariants() {
        assertTrue("GRADLE_GROOVY should be Gradle",
                BuildSystem.GRADLE_GROOVY.isGradle());
        assertTrue("GRADLE_KOTLIN should be Gradle",
                BuildSystem.GRADLE_KOTLIN.isGradle());
        assertFalse("MAVEN should not be Gradle",
                BuildSystem.MAVEN.isGradle());
        assertFalse("UNKNOWN should not be Gradle",
                BuildSystem.UNKNOWN.isGradle());
    }

    @Test
    public void getBuildFile_shouldReturnCorrectFilenames() {
        assertEquals("MAVEN should use pom.xml",
                "pom.xml", BuildSystem.MAVEN.getBuildFile());
        assertEquals("GRADLE_GROOVY should use build.gradle",
                "build.gradle", BuildSystem.GRADLE_GROOVY.getBuildFile());
        assertEquals("GRADLE_KOTLIN should use build.gradle.kts",
                "build.gradle.kts", BuildSystem.GRADLE_KOTLIN.getBuildFile());
        assertNull("UNKNOWN should have null build file",
                BuildSystem.UNKNOWN.getBuildFile());
    }

    @Test
    public void getToolName_shouldReturnHumanReadableNames() {
        assertEquals("MAVEN toolName should be 'Maven'",
                "Maven", BuildSystem.MAVEN.getToolName());
        assertEquals("GRADLE_GROOVY toolName should be 'Gradle (Groovy)'",
                "Gradle (Groovy)", BuildSystem.GRADLE_GROOVY.getToolName());
        assertEquals("GRADLE_KOTLIN toolName should be 'Gradle (Kotlin)'",
                "Gradle (Kotlin)", BuildSystem.GRADLE_KOTLIN.getToolName());
    }
}
