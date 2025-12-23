package com.jakubbone.logsensei.inspection;

import com.intellij.testFramework.LightProjectDescriptor;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NonNls;

public class ServiceMethodInspectionTest extends LightJavaCodeInsightFixtureTestCase {

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
        myFixture.enableInspections(ServiceMethodInspection.class);
        myFixture.copyDirectoryToProject("/stubs", "");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testHighlighting_whenServiceMethodWithoutLogs() {
        myFixture.testHighlighting(false,
                false,
                true,
                "/inspection/service_method/ServiceMissingLogs.java");
    }

    public void testHighlighting_whenServiceCorrectLogs() {
        myFixture.testHighlighting(false,
                false,
                false,
                "/inspection/service_method/ServiceMissingLogs.java");
    }


}
