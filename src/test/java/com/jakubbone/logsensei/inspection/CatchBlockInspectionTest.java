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
        return "src/test/testData";
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(CatchBlockInspection.class);

        // add org/slf4j/* to project temp
        myFixture.copyDirectoryToProject("/stubs", "");
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEmptyCatchHighlighting() {
        myFixture.testHighlighting(false, false, true, "/inspection/catch_block/EmptyCatch.java");
    }

    public void testCatchWithErrorLogHighlighting() {
        myFixture.testHighlighting(false, false, false, "/inspection/catch_block/NonEmptyCatch.java");
    }
}

