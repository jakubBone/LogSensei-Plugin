package com.jakubbone.logsensei.dependency.model;

import lombok.Getter;

@Getter
public enum LoggingLibrary {
    LOG4J2(
            "Log4j2",
            "org.apache.logging.log4j", "log4j-core", "2.22.0",
            "org.apache.logging.log4j", "log4j-api", "2.22.0",
            "Modern high-performance logging framework",
            true,
            "lombok.extern.log4j.Log4j2"
    ),

    SLF4J_LOGBACK(
            "SLF4J + Logback",
            "org.slf4j", "slf4j-api", "2.0.9",
            "ch.qos.logback", "logback-classic", "1.4.14",
            "Flexible logging facade with Logback implementation",
            true,
            "lombok.extern.slf4j.Slf4j"
    ),

    JAVA_UTIL_LOGGING(
            "java.util.logging",
            "java.util.logging", "", "",
            null, null, null,
            "Built-in Java logging",
            false,
            null
    ),

    NONE(
            "None",
            "", "", "",
            null, null, null,
            "No logging library detected",
            false,
            null
    );

    private final String libName;
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String secondaryGroupId;
    private final String secondaryArtifactId;
    private final String secondaryVersion;
    private final String description;
    private final boolean requiresDependency;
    private final String lombokAnnotationFqn;

    LoggingLibrary(String libName,
                   String groupId,
                   String artifactId,
                   String version,
                   String secondaryGroupId,
                   String secondaryArtifactId,
                   String secondaryVersion,
                   String description,
                   boolean requiresDependency,
                   String lombokAnnotationFqn) {
        this.libName = libName;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.secondaryGroupId = secondaryGroupId;
        this.secondaryArtifactId = secondaryArtifactId;
        this.secondaryVersion = secondaryVersion;
        this.description = description;
        this.requiresDependency = requiresDependency;
        this.lombokAnnotationFqn = lombokAnnotationFqn;
    }

    public boolean usesLombokAnnotation() {
        return lombokAnnotationFqn != null && !lombokAnnotationFqn.isEmpty();
    }

    public boolean hasSecondaryDependency() {
        return secondaryGroupId != null && !secondaryGroupId.isEmpty();
    }

    public boolean requiresDependency() {
        return requiresDependency;
    }
}
