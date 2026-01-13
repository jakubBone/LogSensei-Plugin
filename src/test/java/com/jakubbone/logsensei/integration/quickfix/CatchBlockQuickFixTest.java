package com.jakubbone.logsensei.integration.quickfix;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;
import com.jakubbone.logsensei.quickfix.CatchBlockLogQuickFix;

public class CatchBlockQuickFixTest extends LightJavaCodeInsightFixtureTestCase {
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

    public void testQuickFix_shouldAddErrorLog_whenCatchEmpty(){
        PsiFile file = myFixture.configureByText("Test.java", """
                public class Test {
                    public void test(){
                        try {
                           risky();
                        } catch (IOException e) {
                      
                        }
                    }
                }
                """);

        String text = getFileTextAfterQuickFix(file);

        assertTrue("should contain error log", text.contains("log.error"));
        assertTrue("Should add @Slf4j", text.contains("@Slf4j"));
    }

    public void testQuickFix_shouldAddErrorLog_beforeExistingStatement(){
        PsiFile file = myFixture.configureByText("Test.java", """
                public class Test {
                    public void test(){
                        try {
                           risky();
                        } catch (IOException e) {
                           throw new RuntimeException(e);
                        }
                    }
                }
                """);

        String text = getFileTextAfterQuickFix(file);

        assertTrue("should contain error log", text.contains("log.error"));
        assertTrue("Should add @Slf4j", text.contains("@Slf4j"));
    }

    private String getFileTextAfterQuickFix(PsiFile file) {
        PsiElement keyword = findCatchKeyword(file);
        assertNotNull("should find catch keyword", keyword);

        CatchBlockLogQuickFix quickFix = new CatchBlockLogQuickFix();

        WriteCommandAction.runWriteCommandAction(getProject(), () -> {
            quickFix.addLog(getProject(), keyword, LoggingLibrary.SLF4J_LOGBACK);
        });

        return file.getText();
    }

    private PsiElement findCatchKeyword(PsiFile file) {
        return PsiTreeUtil.findChildrenOfType(file, PsiKeyword.class)
                .stream()
                .filter(k -> "catch".equals(k.getText()))
                .findFirst()
                .orElse(null);
    }
}
