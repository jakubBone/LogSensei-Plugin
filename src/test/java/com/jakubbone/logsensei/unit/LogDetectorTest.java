package com.jakubbone.logsensei.unit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jakubbone.logsensei.inspection.detector.LogDetector;
import org.junit.Test;

public class LogDetectorTest {

    @Test
    public void containsLogCall_shouldDetectSlf4jPatterns() {
        assertTrue("Should detect log.error",
                LogDetector.containsLogCall("log.error(\"msg\", e);"));
        assertTrue("Should detect log.warn",
                LogDetector.containsLogCall("log.warn(\"msg\");"));
        assertTrue("Should detect log.info",
                LogDetector.containsLogCall("log.info(\"msg\");"));
        assertTrue("Should detect log.debug",
                LogDetector.containsLogCall("log.debug(\"msg\");"));
    }

    @Test
    public void containsLogCall_shouldDetectLoggerVariant() {
        assertTrue("Should detect logger.info",
                LogDetector.containsLogCall("logger.info(\"msg\");"));
        assertTrue("Should detect logger.error",
                LogDetector.containsLogCall("logger.error(\"msg\");"));
    }

    @Test
    public void containsLogCall_shouldDetectSystemOut() {
        assertTrue("Should detect System.out.println",
                LogDetector.containsLogCall("System.out.println(\"msg\");"));
        assertTrue("Should detect System.out.print",
                LogDetector.containsLogCall("System.out.print(x);"));
    }

    @Test
    public void containsLogCall_shouldReturnFalse_whenNoLogPresent() {
        assertFalse("Should not detect regular method call",
                LogDetector.containsLogCall("doSomething();"));
        assertFalse("Should not detect variable declaration",
                LogDetector.containsLogCall("String x = \"test\";"));
        assertFalse("Should not detect return statement",
                LogDetector.containsLogCall("return result;"));
    }
}
