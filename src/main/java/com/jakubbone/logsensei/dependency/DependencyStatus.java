package com.jakubbone.logsensei.dependency;

import lombok.Getter;

@Getter
public class DependencyStatus {
    private boolean hasLombok;
    private LoggingLibrary detectedLoggingLibrary;
    private boolean buildSystem;

    public DependencyStatus(boolean hasLombok, LoggingLibrary detectedLoggingLibrary, boolean buildSystem) {
        this.hasLombok = hasLombok;
        this.detectedLoggingLibrary = detectedLoggingLibrary;
        this.buildSystem = buildSystem;
    }

    public boolean hasLoggingLibrary() {
        return detectedLoggingLibrary != LoggingLibrary.NONE;
    }

    public boolean isFullConfigured(){
        return hasLombok && hasLoggingLibrary();
    }

    public boolean needsConfiguration(){
        return !hasLombok || !hasLoggingLibrary();
    }

    @Override
    public String toString() {
        return "DependencyStatus{" +
                ", hasLombok=" + hasLombok +
                ", hasLoggingLibrary=" + hasLoggingLibrary() +
                ", buildSystem=" + buildSystem +
                "}";
    }
}
