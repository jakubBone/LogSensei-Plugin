package com.jakubbone.logsensei.quickfix;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

public class LoopQuickFixTest extends LightJavaCodeInsightFixtureTestCase {
    /*@Override
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

    public void testQuickFix_shouldAddLog(){
        PsiFile file = myFixture.configureByText("Test.java", """
                public class Test {
                   public void process(){
                        for(int i = 0; i < 10; i++){
                            log.info("excessive info log");
                        }
                   }
                }
                """);

        String text = getFileTextAfterQuickFix(file);

        assertTrue("should contain error log", text.contains("log.error"));
        assertTrue("Should add @Slf4j", text.contains("@lombok.extern.slf4j.Slf4j"));
    }

    private String getFileTextAfterQuickFix(PsiFile file) {
        PsiElement keyword = findLoopKeyword(file);

        assertNotNull(keyword);

        LoopLogQuickFix quickFix = new LoopLogQuickFix();

    }

    private PsiElement findLoopKeyword(PsiFile file) {
        return PsiTreeUtil.findChildrenOfType(file, PsiKeyword.class)
                .stream()
                .filter(k -> "catch".equals(k.getText()))
                .findFirst()
                .orElse(null);
    }*/
}
