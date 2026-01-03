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
        assertTrue(BuildSystem.MAVEN.isMaven());
        assertFalse(BuildSystem.GRADLE_GROOVY.isMaven());
        assertFalse(BuildSystem.GRADLE_KOTLIN.isMaven());
        assertFalse(BuildSystem.UNKNOWN.isMaven());
    }

    @Test
    public void isGradle_shouldReturnTrueForBothVariants() {
        assertTrue(BuildSystem.GRADLE_GROOVY.isGradle());
        assertTrue(BuildSystem.GRADLE_KOTLIN.isGradle());
        assertFalse(BuildSystem.MAVEN.isGradle());
        assertFalse(BuildSystem.UNKNOWN.isGradle());
    }

    @Test
    public void getBuildFile_shouldReturnCorrectFilenames() {
        assertEquals("pom.xml", BuildSystem.MAVEN.getBuildFile());
        assertEquals("build.gradle", BuildSystem.GRADLE_GROOVY.getBuildFile());
        assertEquals("build.gradle.kts", BuildSystem.GRADLE_KOTLIN.getBuildFile());
        assertNull(BuildSystem.UNKNOWN.getBuildFile());
    }

    @Test
    public void getToolName_shouldReturnHumanReadableNames() {
        assertEquals("Maven", BuildSystem.MAVEN.getToolName());
        assertEquals("Gradle (Groovy)", BuildSystem.GRADLE_GROOVY.getToolName());
        assertEquals("Gradle (Kotlin)", BuildSystem.GRADLE_KOTLIN.getToolName());
    }
}
