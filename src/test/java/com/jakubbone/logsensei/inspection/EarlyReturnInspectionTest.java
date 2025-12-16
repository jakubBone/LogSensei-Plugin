package com.jakubbone.logsensei.inspection;

import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
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

    public void testHighlighting_whenEmptyEarlyReturn() {
        myFixture.testHighlighting(false, false, true, "/inspection/early_return/EmptyEarlyReturn.java");
    }

    public void testHighlighting_whenNonEmptyEarlyReturn() {
        myFixture.testHighlighting(false, false, false , "/inspection/early_return/NonEmptyEarlyReturn.java");
    }
}
