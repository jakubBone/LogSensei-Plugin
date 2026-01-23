package com.jakubbone.logsensei.integration.quickfix;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;
import com.jakubbone.logsensei.quickfix.EntryExitLogQuickFix;

public class EntryExitLogQuickFixTest extends LightJavaCodeInsightFixtureTestCase {
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
        myFixture.copyDirectoryToProject("/stubs", "");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // Service tests
    public void testQuickFix_serviceMethod_shouldAddEntryLog() {
        assertSpringComponent("Service", """
                    doWork();
                    log.info("exit");
            """);
    }

    public void testQuickFix_serviceMethod_shouldAddExitLog() {
        assertSpringComponent("Service", """
                    log.info("enter");
                    doWork();
            """);
    }

    public void testQuickFix_serviceMethod_shouldAddBothLogs() {
        assertSpringComponent("Service", """
                    doWork();
            """);
    }

    // RestController tests
    public void testQuickFix_restController_shouldAddEntryLog() {
        assertRestController("""
                    doWork();
                    log.info("exit");
            """);
    }

    public void testQuickFix_restController_shouldAddExitLog() {
        assertRestController("""
                    log.info("enter");
                    doWork();
            """);
    }

    public void testQuickFix_restController_shouldAddBothLogs() {
        assertRestController("""
                    doWork();
            """);
    }

    // Controller tests
    public void testQuickFix_controller_shouldAddBothLogs() {
        assertController("""
                    doWork();
            """);
    }

    private void assertSpringComponent(String annotation, String body) {
        PsiFile file = myFixture.configureByText("TestComponent.java", """
            import org.springframework.stereotype.%s;
            @%s
            public class TestComponent {
                public void process() {
                     %s
                }
                private void doWork() {}
            }
            """.formatted(annotation, annotation, body));

        assertQuickFixAddsLogs(file);
    }

    private void assertRestController(String body) {
        PsiFile file = myFixture.configureByText("TestController.java", """
            import org.springframework.web.bind.annotation.RestController;
            @RestController
            public class TestController {
                public void process() {
                     %s
                }
                private void doWork() {}
            }
            """.formatted(body));

        assertQuickFixAddsLogs(file);
    }

    private void assertController(String body) {
        PsiFile file = myFixture.configureByText("TestController.java", """
            import org.springframework.stereotype.Controller;
            @Controller
            public class TestController {
                public void process() {
                     %s
                }
                private void doWork() {}
            }
            """.formatted(body));

        assertQuickFixAddsLogs(file);
    }

    private void assertQuickFixAddsLogs(PsiFile file) {
        PsiElement methodId = findMethodIdentifier(file, "process");

        EntryExitLogQuickFix quickFix = new EntryExitLogQuickFix(false, false);

        WriteCommandAction.runWriteCommandAction(getProject(), () -> {
            quickFix.addLog(getProject(), methodId, LoggingLibrary.SLF4J_LOGBACK);
        });

        String result = file.getText();
        assertTrue("Should contain entry log", result.contains("Operation started"));
        assertTrue("Should contain exit log", result.contains("Operation finished"));
    }

    private PsiElement findMethodIdentifier(PsiFile file, String methodName) {
        PsiMethod method = PsiTreeUtil.findChildrenOfType(file, PsiMethod.class)
                .stream()
                .filter(m -> methodName.equals(m.getName()))
                .findFirst()
                .orElse(null);

        return method != null ? method.getNameIdentifier() : null;
    }
}
