package com.jakubbone.logsensei.dependency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.jakubbone.logsensei.dependency.model.LoggingLibrary;
import org.junit.Test;

public class LoggingLibraryTest {
    @Test
    public void usesLombokAnnotation_shouldReturnTrueForLog4j2AndSlf4j() {
        assertTrue("LOG4J2 should use Lombok annotation",
                LoggingLibrary.LOG4J2.usesLombokAnnotation());
        assertTrue("SLF4J_LOGBACK should use Lombok annotation",
                LoggingLibrary.SLF4J_LOGBACK.usesLombokAnnotation());
    }

    @Test
    public void usesLombokAnnotation_shouldReturnFalseForJulAndNone() {
        assertFalse("JAVA_UTIL_LOGGING should not use Lombok",
                LoggingLibrary.JAVA_UTIL_LOGGING.usesLombokAnnotation());
        assertFalse("NONE should not use Lombok",
                LoggingLibrary.NONE.usesLombokAnnotation());
    }

    @Test
    public void getLombokAnnotationFqn_shouldReturnCorrectFqn() {
        assertEquals("LOG4J2 should use @Log4j2 annotation",
                "lombok.extern.log4j.Log4j2",
                LoggingLibrary.LOG4J2.getLombokAnnotationFqn());
        assertEquals("SLF4J_LOGBACK should use @Slf4j annotation",
                "lombok.extern.slf4j.Slf4j",
                LoggingLibrary.SLF4J_LOGBACK.getLombokAnnotationFqn());
        assertNull("JAVA_UTIL_LOGGING should have null annotation FQN",
                LoggingLibrary.JAVA_UTIL_LOGGING.getLombokAnnotationFqn());
    }

    @Test
    public void requiresDependency_shouldReturnTrueForExternalLibraries() {
        assertTrue("LOG4J2 requires external dependency",
                LoggingLibrary.LOG4J2.requiresDependency());
        assertTrue("SLF4J_LOGBACK requires external dependency",
                LoggingLibrary.SLF4J_LOGBACK.requiresDependency());
    }

    @Test
    public void requiresDependency_shouldReturnFalseForBuiltInAndNone() {
        assertFalse("JAVA_UTIL_LOGGING is built-in, no dependency needed",
                LoggingLibrary.JAVA_UTIL_LOGGING.requiresDependency());
        assertFalse("NONE should not require dependency",
                LoggingLibrary.NONE.requiresDependency());
    }

    @Test
    public void hasSecondaryDependency_shouldReturnTrueForLog4j2AndSlf4j() {
        assertTrue("LOG4J2 has secondary dependency (log4j-api)",
                LoggingLibrary.LOG4J2.hasSecondaryDependency());
        assertTrue("SLF4J_LOGBACK has secondary dependency (logback-classic)",
                LoggingLibrary.SLF4J_LOGBACK.hasSecondaryDependency());
    }

    @Test
    public void hasSecondaryDependency_shouldReturnFalseForJulAndNone() {
        assertFalse("JAVA_UTIL_LOGGING has no secondary dependency",
                LoggingLibrary.JAVA_UTIL_LOGGING.hasSecondaryDependency());
        assertFalse("NONE has no secondary dependency",
                LoggingLibrary.NONE.hasSecondaryDependency());
    }

    @Test
    public void log4j2_shouldHaveCorrectMavenCoordinates() {
        assertEquals("LOG4J2 groupId should be Apache Log4j",
                "org.apache.logging.log4j", LoggingLibrary.LOG4J2.getGroupId());
        assertEquals("LOG4J2 artifactId should be log4j-core",
                "log4j-core", LoggingLibrary.LOG4J2.getArtifactId());
        assertNotNull("LOG4J2 version should not be null",
                LoggingLibrary.LOG4J2.getVersion());
    }

    @Test
    public void slf4j_shouldHaveCorrectMavenCoordinates() {
        assertEquals("SLF4J_LOGBACK groupId should be org.slf4j",
                "org.slf4j", LoggingLibrary.SLF4J_LOGBACK.getGroupId());
        assertEquals("SLF4J_LOGBACK artifactId should be slf4j-api",
                "slf4j-api", LoggingLibrary.SLF4J_LOGBACK.getArtifactId());
    }
}
