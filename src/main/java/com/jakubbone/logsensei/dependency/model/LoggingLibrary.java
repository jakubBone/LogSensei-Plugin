package com.jakubbone.logsensei.dependency.model;

import lombok.Getter;

@Getter
public enum LoggingLibrary {
    LOG4J2("Log4j2", "org.apache.logging.log4j", "log4j-core", "2.22.0",
            "Modern, high-performance logging framework", true),
    SLF4J_LOGBACK("SLF4J + Logback", "org.slf4j", "slf4j-api", "2.0.9",
            "Flexible logging facade with Logback implementation", true),
    JAVA_UTIL_LOGGING("java.util.logging", "java.util.logging", "", "",
            "Built-in Java logging (no external dependencies)", false),
    NONE("None", "", "", "", "No logging library detected", false);


    private final String libName;
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String description;
    private final boolean requiresDependency;

    LoggingLibrary(String libName, String groupId, String artifactId, String version, String description, boolean requiresDependency) {
        this.libName = libName;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.description = description;
        this.requiresDependency = requiresDependency;
    }

    public String getSecondaryArtifactId() {
        if (this == SLF4J_LOGBACK) {
            return "logback-classic";
        }
        return null;
    }

    public String getSecondaryGroupId() {
        if (this == SLF4J_LOGBACK) {
            return "ch.qos.logback";
        }
        return null;
    }

    public String getSecondaryVersion() {
        if (this == SLF4J_LOGBACK) {
            return "1.4.14";
        }
        return null;
    }
}
