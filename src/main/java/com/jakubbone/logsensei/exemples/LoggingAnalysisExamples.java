package com.jakubbone.logsensei.exemples;

import lombok.extern.log4j.Log4j2;

/**
 * Java code examples for PSI analysis and logging pattern testing.
 *
 * This class contains all the scenarios that LogSensei should detect and analyze.
 * Each method represents a different test case from the plugin requirements.
 * PSI Viewer analyze them to understand the PSI structure of each case.
 */

@Log4j2
public class LoggingAnalysisExamples {
    // ================================
    // 1. CATCH BLOCKS
    // ================================

    // Expected: LogSensei should suggest adding log.error()
    public void emptyCatchBlock() {
        try {
            int result = 10 / 0;
        } catch (Exception ex) {

        }
    }

    // Expected: LogSensei should NOT flag this
    public void catchWithLogging() {
        try {
            int result = 10 / 0;
        } catch (Exception ex) {
            log.error("An error occurred: {}", ex.getMessage());
        }
    }
}
