package com.jakubbone.logsensei.integration.inspection;

import com.intellij.testFramework.LightProjectDescriptor;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.jakubbone.logsensei.inspection.ControllerInspection;
import org.jetbrains.annotations.NonNls;

public class ControllerInspectionTest extends LightJavaCodeInsightFixtureTestCase {

    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return JAVA_17;
    }

    @Override
    protected @NonNls String getTestDataPath() {
        return "src/test/testData";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(ControllerInspection.class);
        myFixture.copyDirectoryToProject("/stubs", "");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testHighlighting_whenRestControllerMethodNoLogs() {
        myFixture.testHighlighting(false,
                false,
                true,
                "/inspection/controller/RestControllerNoLogs.java");
    }

    public void testHighlighting_whenControllerMethodNoLogs() {
        myFixture.testHighlighting(false,
                false,
                true,
                "/inspection/controller/ControllerNoLogs.java");
    }

    public void testHighlighting_whenControllerWithLogs() {
        myFixture.testHighlighting(false,
                false,
                false,
                "/inspection/controller/ControllerWithLogs.java");
    }

    public void testHighlighting_whenNonControllerClass() {
        myFixture.testHighlighting(false,
                false,
                false,
                "/inspection/controller/NonControllerClass.java");
    }
}
