package com.jakubbone.logsensei.dependency;

public enum LoggingLibrary {
    LOG4J2("Log4j2", "org.apache.logging.log4j.Logger"),
    SLF4J("SLF4J", "org.slf4j.Logger"),
    LOGBACK("Logback", "ch.qos.logback.classic.Logger"),
    JUL("Java Util Logging", "java.util.logging.Logger"),
    NONE("None", null);

    private final String libName;
    private final String className;

    LoggingLibrary(String libName, String className) {
        this.libName = libName;
        this.className = className;
    }
}
