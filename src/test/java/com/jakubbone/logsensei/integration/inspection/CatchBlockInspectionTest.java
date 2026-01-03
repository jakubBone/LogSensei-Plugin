package com.jakubbone.logsensei.integration.inspection;

import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.jakubbone.logsensei.inspection.CatchBlockInspection;

public class CatchBlockInspectionTest extends LightJavaCodeInsightFixtureTestCase {

    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return JAVA_17;
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(CatchBlockInspection.class);
        myFixture.copyDirectoryToProject("/stubs", "");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testHighlighting_whenCatchNoLogs() {
        myFixture.testHighlighting(false,
                false,
                true,
                "/inspection/catch_block/CatchNoLogs.java");
    }

    public void testHighlighting_whenCatchWithLogs() {
        myFixture.testHighlighting(
                false,
                false,
                false,
                "/inspection/catch_block/CatchWithLogs.java");
    }
}

