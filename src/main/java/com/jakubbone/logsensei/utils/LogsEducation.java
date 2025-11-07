package com.jakubbone.logsensei.utils;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class LogsEducation {

    public static void showErrorLevelEducation(Project project) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("LogSensei")
                .createNotification(
                        "LogSensei: ERROR Level",
                        "ERROR should be used for exceptions and critical failures that prevent normal operation.\n" +
                                "Examples: database connection failure, unhandled exceptions, data corruption.",
                        NotificationType.INFORMATION
                )
                .notify(project);
    }

    public static void showWarnLevelEducation(Project project){
        NotificationGroupManager.getInstance()
                .getNotificationGroup("LogSensei")
                .createNotification(
                        "LogSensei: WARN Level",
                        "WARN should be used for unexpected but handled situations that might indicate problems.\n" +
                                "Examples: null values where objects expected, deprecated API usage, recoverable errors.",
                )
                .notify(project);
    }

    public static void showInfoLevelEducation(Project project){
        NotificationGroupManager.getInstance()
                .getNotificationGroup("LogSensei")
                .createNotification("LogSensei: INFO Level",
                        "INFO should be used for important business events and significant state changes.\n" +
                                "Examples: user login, order created, payment processed, service started/stopped.",
                        NotificationType.INFORMATION
                )
                .notify(project);
    }

    public static void showDebugLevelEducation(Project project){
        NotificationGroupManager.getInstance()
                .getNotificationGroup("LogSensei")
                .createNotification(
                        "LogSensei: DEBUG Level",
                        "DEBUG should be used for detailed technical information useful during development.\n" +
                                "Examples: method entry/exit, loop iterations, variable values, flow tracking.",
                        NotificationType.INFORMATION
                )
                .notify(project);
    }
}
