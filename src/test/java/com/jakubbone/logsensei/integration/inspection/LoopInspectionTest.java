package com.jakubbone.logsensei.integration.inspection;

import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.jakubbone.logsensei.inspection.LoopInspection;
import org.jetbrains.annotations.NotNull;

public class LoopInspectionTest extends LightJavaCodeInsightFixtureTestCase {
    @Override
    protected @NotNull LightProjectDescriptor getProjectDescriptor() {
        return JAVA_17;
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(LoopInspection.class);
        myFixture.copyDirectoryToProject("/stubs", "");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testHighlighting_whenLoopWithInfoLog() {
        myFixture.testHighlighting(
                false,
                false,
                true,
                "/inspection/loop_logging/LoopWithInfoLog.java");
    }

    public void testHighlighting_whenLoopWithDebugWarnLogs() {
        myFixture.testHighlighting(
                false,
                false,
                false,
                "/inspection/loop_logging/LoopWithDebugWarnLogs.java");
    }
}
