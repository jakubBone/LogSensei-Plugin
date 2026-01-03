package com.jakubbone.logsensei.integration.inspection;

import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.jakubbone.logsensei.inspection.EarlyReturnInspection;
import org.jetbrains.annotations.NotNull;

public class EarlyReturnInspectionTest extends LightJavaCodeInsightFixtureTestCase {
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
        myFixture.enableInspections(EarlyReturnInspection.class);
        myFixture.copyDirectoryToProject("/stubs", "");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testHighlighting_whenEarlyReturnNoLogs() {
        myFixture.testHighlighting(
                false,
                false,
                true,
                "/inspection/early_return/EarlyReturnNoLogs.java");
    }

    public void testHighlighting_whenEarlyReturnWithLogs() {
        myFixture.testHighlighting(false,
                false,
                false,
                "/inspection/early_return/EarlyReturnWithLogs.java");
    }
}
