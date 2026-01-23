package com.jakubbone.logsensei.integration.quickfix;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;
import com.jakubbone.logsensei.quickfix.ControllerLogQuickFix;

public class ControllerQuickFixTest extends LightJavaCodeInsightFixtureTestCase {
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

    public void testQuickFix_shouldAddLogBeforeStatement(){
        assertControllerMethod("""
                    doWork();
                    log.info("exit");
            """);
    }

    public void testQuickFix_shouldAddLogAfterStatement(){
        assertControllerMethod("""
                    log.info("enter");
                    doWork();
            """);

    }

    public void testQuickFix_shouldLogsBeforeAndAfterStatement(){
        assertControllerMethod("""
                    doWork();
            """);
    }

    public void testQuickFix_withControllerAnnotation(){
        PsiFile file = myFixture.configureByText("TestController.java", """
            import org.springframework.stereotype.Controller;
            @Controller
            public class TestController {
                public void process() {
                     doWork();
                }
                private void doWork() {}
            }
            """);

        PsiElement methodId = findMethodIdentifier(file, "process");

        ControllerLogQuickFix quickFix = new ControllerLogQuickFix(false, false);

        WriteCommandAction.runWriteCommandAction(getProject(), () -> {
            quickFix.addLog(getProject(), methodId, LoggingLibrary.SLF4J_LOGBACK);
        });

        String result = file.getText();
        assertTrue("Should contain entry log", result.contains("Operation started"));
        assertTrue("Should contain exit log", result.contains("Operation finished"));
    }

    private void assertControllerMethod(String body){
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

        PsiElement methodId = findMethodIdentifier(file, "process");

        ControllerLogQuickFix quickFix = new ControllerLogQuickFix(false, false);

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
