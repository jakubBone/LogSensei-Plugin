package com.jakubbone.logsensei.exemples;

/**
 * Java code examples for PSI analysis and logging pattern testing.
 *
 * This class contains all the scenarios that LogSensei should detect and analyze.
 * Each method represents a different test case from the plugin requirements.
 * PSI Viewer analyze them to understand the PSI structure of each case.
 */
public class LoggingAnalysisExamples {

    // Expected: LogSensei should suggest adding log.error()
    public void emptyCatchBlock() {
        try {
            int result = 10 / 0;
        } catch (Exception e) {
            // Empty catch
        }
    }
}
