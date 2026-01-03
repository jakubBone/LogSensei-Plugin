package com.jakubbone.logsensei.integration.inspection;

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

    public void testHighlighting_whenServiceMethodNoLogs() {
        myFixture.testHighlighting(false,
                false,
                true,
                "/inspection/service_method/ServiceNoLogs.java");
    }

    public void testHighlighting_whenServiceWithLogs() {
        myFixture.testHighlighting(false,
                false,
                false,
                "/inspection/service_method/ServiceWithLogs.java");
    }

    public void testHighlighting_whenNonServiceClass() {
        myFixture.testHighlighting(false,
                false,
                false,
                "/inspection/service_method/NonServiceClass.java");
    }
}
