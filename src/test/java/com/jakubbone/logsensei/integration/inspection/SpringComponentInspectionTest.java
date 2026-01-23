package com.jakubbone.logsensei.integration.inspection;

import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.jakubbone.logsensei.inspection.SpringComponentInspection;
import org.jetbrains.annotations.NonNls;

public class SpringComponentInspectionTest extends LightJavaCodeInsightFixtureTestCase {

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
        myFixture.enableInspections(SpringComponentInspection.class);
        myFixture.copyDirectoryToProject("/stubs", "");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // Service tests
    public void testHighlighting_whenServiceMethodNoLogs() {
        myFixture.testHighlighting(false,
                false,
                true,
                "/inspection/spring_component/ServiceNoLogs.java");
    }

    public void testHighlighting_whenServiceWithLogs() {
        myFixture.testHighlighting(false,
                false,
                false,
                "/inspection/spring_component/ServiceWithLogs.java");
    }

    // Controller tests
    public void testHighlighting_whenRestControllerMethodNoLogs() {
        myFixture.testHighlighting(false,
                false,
                true,
                "/inspection/spring_component/RestControllerNoLogs.java");
    }

    public void testHighlighting_whenControllerMethodNoLogs() {
        myFixture.testHighlighting(false,
                false,
                true,
                "/inspection/spring_component/ControllerNoLogs.java");
    }

    public void testHighlighting_whenControllerWithLogs() {
        myFixture.testHighlighting(false,
                false,
                false,
                "/inspection/spring_component/ControllerWithLogs.java");
    }

    // Non-component test
    public void testHighlighting_whenNonSpringComponent() {
        myFixture.testHighlighting(false,
                false,
                false,
                "/inspection/spring_component/NonSpringComponent.java");
    }
}
