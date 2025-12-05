package com.jakubbone.logsensei.dependency.model;

import lombok.Getter;

@Getter
public class DependencyStatus {
    private boolean hasLombok;
    private LoggingLibrary detectedLoggingLibrary;
    private BuildSystem buildSystem;

    public DependencyStatus(boolean hasLombok, LoggingLibrary detectedLoggingLibrary, BuildSystem buildSystem) {
        this.hasLombok = hasLombok;
        this.detectedLoggingLibrary = detectedLoggingLibrary;
        this.buildSystem = buildSystem;
    }

    public boolean hasLombok(){
        return hasLombok;
    }

    public boolean hasLoggingLibrary() {
        return detectedLoggingLibrary != LoggingLibrary.NONE;
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
