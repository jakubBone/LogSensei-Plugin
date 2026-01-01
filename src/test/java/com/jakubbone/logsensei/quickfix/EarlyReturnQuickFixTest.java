package com.jakubbone.logsensei.quickfix;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;

public class EarlyReturnQuickFixTest extends LightJavaCodeInsightFixtureTestCase {
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

    public void testQuickFix_shouldAddDebugLog(){
        PsiFile file = myFixture.configureByText("Test.java", """
                public class Test {
                    public void test(int numb){
                        if(numb == 0){
                            return;
                        }
                    }
                }
                """);

        String text = getFileTextAfterQuickFix(file);

        assertTrue("should contain error log", text.contains("log.debug"));
        assertTrue("", text.contains("@lombok.extern.slf4j.Slf4j"));

    }

    private String getFileTextAfterQuickFix(PsiFile file) {
        PsiElement returnKeyword = findReturn(file);

        assertNotNull(returnKeyword);

        EarlyReturnLogQuickFix quickFix = new EarlyReturnLogQuickFix();

        WriteCommandAction.runWriteCommandAction(getProject(),() -> {
            quickFix.addLog(getProject(), returnKeyword, LoggingLibrary.SLF4J_LOGBACK);
        });

        return file.getText();
    }

    private PsiElement findReturn(PsiFile file) {
        return PsiTreeUtil.findChildrenOfType(file, PsiKeyword.class)
                .stream()
                .filter(k -> "return".equals(k.getText()))
                .findFirst()
                .orElse(null);
    }
}
