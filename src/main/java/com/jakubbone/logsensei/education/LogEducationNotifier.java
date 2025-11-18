package com.jakubbone.logsensei.education;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class LogEducationNotifier {

    private static void notify(Project project, String title, String htmlContent) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("LogSensei")
                .createNotification(title, htmlContent, NotificationType.INFORMATION)
                .notify(project);
    }

    public static void showErrorLevelEducation(Project project) {
        notify(
                project,
                "🎓 LogSensei: understanding ERROR Level",
                "ERROR should be used for exceptions and critical failures that prevent normal operation.<br/>" +
                        "<br/>\uD83D\uDD34 <b>Examples</b><br/>" +
                        "• database connection failure<br/>" +
                        "• unhandled exceptions<br/>" +
                        "• data corruption"
        );
    }

    public static void showWarnLevelEducation(Project project) {
        notify(
                project,
                "🎓 LogSensei: understanding WARN Level",
                "WARN should be used for unexpected but handled situations that might indicate problems.<br/>" +
                        "<br/>\uD83D\uDFE0 <b>Examples</b><br/>" +
                        "• null values where objects expected<br/>" +
                        "• deprecated API usage<br/>" +
                        "• recoverable errors"
        );
    }

    public static void showInfoLevelEducation(Project project) {
        notify(
                project,
                "🎓 LogSensei: understanding INFO Level",
                "INFO should be used for important business events and significant state changes.<br/>" +
                        "<br/>\uD83D\uDFE6 <b>Examples</b><br/>" +
                        "• user login<br/>" +
                        "• order created<br/>" +
                        "• payment processed<br/>" +
                        "• service started/stopped"
        );
    }

    public static void showDebugLevelEducation(Project project) {
        notify(
                project,
                "🎓 LogSensei: understanding DEBUG Level",
                "DEBUG should be used for detailed technical information useful during development.<br/>" +
                        "<br/>\uD83D\uDFE2 <b>Examples</b><br/>" +
                        "• method entry/exit<br/>" +
                        "• loop iterations<br/>" +
                        "• variable values<br/>" +
                        "• flow tracking"
        );
    }
}
