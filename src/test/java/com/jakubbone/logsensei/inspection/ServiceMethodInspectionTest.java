package com.jakubbone.logsensei.inspection;

import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NonNls;

public class ServiceMethodInspectionTest extends JavaCodeInsightFixtureTestCase {

    @Override
    protected @NonNls String getTestDataPath() {
        return "src/test/testData";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(ServiceMethodInspection.class);
        myFixture.copyDirectoryToProject("/stubs", "");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*public void testHighlighting_whenNoLogsInServiceMethod() {
        myFixture.testHighlighting(false,
                false,
                true,
                "/inspection/service_method/ServiceMethod.java");
    }*/
}
