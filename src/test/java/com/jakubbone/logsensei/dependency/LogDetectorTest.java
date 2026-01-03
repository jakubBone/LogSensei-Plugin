package com.jakubbone.logsensei.dependency;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jakubbone.logsensei.inspection.detector.LogDetector;
import org.junit.Test;

public class LogDetectorTest {

    @Test
    public void containsLogCall_shouldDetectSlf4jPatterns() {
        assertTrue(LogDetector.containsLogCall("log.error(\"msg\", e);"));
        assertTrue(LogDetector.containsLogCall("log.warn(\"msg\");"));
        assertTrue(LogDetector.containsLogCall("log.info(\"msg\");"));
        assertTrue(LogDetector.containsLogCall("log.debug(\"msg\");"));
    }

    @Test
    public void containsLogCall_shouldDetectLoggerVariant() {
        assertTrue(LogDetector.containsLogCall("logger.info(\"msg\");"));
        assertTrue(LogDetector.containsLogCall("logger.error(\"msg\");"));
    }

    @Test
    public void containsLogCall_shouldDetectSystemOut() {
        assertTrue(LogDetector.containsLogCall("System.out.println(\"msg\");"));
        assertTrue(LogDetector.containsLogCall("System.out.print(x);"));
    }

    @Test
    public void containsLogCall_shouldReturnFalse_whenNoLogPresent() {
        assertFalse(LogDetector.containsLogCall("doSomething();"));
        assertFalse(LogDetector.containsLogCall("String x = \"test\";"));
        assertFalse(LogDetector.containsLogCall("return result;"));
    }
}
