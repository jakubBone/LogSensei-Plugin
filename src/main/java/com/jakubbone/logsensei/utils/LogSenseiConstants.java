package com.jakubbone.logsensei.utils;

public class LogSenseiConstants {
    public static final String SERVICE_ANNOTATION = "org.springframework.stereotype.Service";
    public static final String LOG_PATTERN_ERROR = "log.error(\"[{}] An exception occurred.\", \"%s\", e);";
    public static final String LOG_PATTERN_DEBUG = "log.debug(\"[%s] Early return\");";
    public static final String LOG_PATTERN_WARN = "log.warn(\"[%s] Variable '%s' is null\", \"%s\");";
}
