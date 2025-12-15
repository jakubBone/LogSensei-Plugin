package com.jakubbone.logsensei.inspection;

import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

public class CatchBlockInspectionTest extends LightJavaCodeInsightFixtureTestCase {

    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return JAVA_17;
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/inspection/catch";
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(CatchBlockInspection.class);

        myFixture.addFileToProject("org/slf4j/Logger.java", """
            package org.slf4j;
            public interface Logger {
                void error(String msg, Exception e);
                void info(String msg);
                void debug(String msg);
            }
        """);

        myFixture.addFileToProject("org/slf4j/LoggerFactory.java", """
            package org.slf4j;
            public class LoggerFactory {
                public static Logger getLogger(Class<?> clazz) { return null; }
            }
        """);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }


    public void testEmptyCatchHighlighting() {
        myFixture.testHighlighting(false, false, true, "EmptyCatch.java");
    }

    public void testCatchWithErrorLogHighlighting() {
        myFixture.testHighlighting(false, false, false, "NonEmptyCatch.java");
    }
}

