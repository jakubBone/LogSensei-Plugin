package com.jakubbone.logsensei.quickfix;

import java.util.List;
import java.util.stream.Collectors;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiLoopStatement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.jakubbone.logsensei.dependency.model.LoggingLibrary;

public class LoopQuickFixTest extends LightJavaCodeInsightFixtureTestCase {
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

    public void testQuickFix_shouldAddLog(){
        PsiFile file = myFixture.configureByText("Test.java", """ 
                class Test {
                     void test() {
                          for (int i = 0; i < 3; i++) {
                               log.info("for {}", i);
                          }
                          
                          for (int x : List.of(1,2,3)) {
                              log.info("foreach {}", x);
                          }
                          
                          int j = 0;
                          while (j < 3) {
                              log.info("while {}", j);
                              j++;
                          }
                          
                          do {
                            log.info("do {}", j);
                            j--;
                          } while (j > 0);

                     }
                }
                """);

        String text = getFileTextAfterQuickFix(file);

        assertTrue("should contain debug log", text.contains("log.debug"));
        assertTrue("Should add @Slf4j", text.contains("@lombok.extern.slf4j.Slf4j"));
    }

    private String getFileTextAfterQuickFix(PsiFile file) {
        List<PsiLoopStatement> loops = findAllLoops(file);
        assertFalse("should find loops", loops.isEmpty());

        List<PsiMethodCallExpression> calls = createInfoLogCalls(file);
        assertFalse("should find info calls", calls.isEmpty());

        LoopLogQuickFix quickFix = new LoopLogQuickFix(calls);

        WriteCommandAction.runWriteCommandAction(getProject(),() -> {
            for (PsiLoopStatement loop : loops) {
                PsiElement keyword = findLoopKeyword(loop);
                assertNotNull(keyword);
                quickFix.addLog(getProject(), keyword, LoggingLibrary.SLF4J_LOGBACK);
            }
        } );

        return file.getText();
    }

    private List<PsiMethodCallExpression> createInfoLogCalls(PsiFile file){
        return PsiTreeUtil.findChildrenOfType(file, PsiMethodCallExpression.class)
                        .stream()
                        .filter(call -> call.getText().contains(".info("))
                        .collect(Collectors.toList());
    }

    private List<PsiLoopStatement> findAllLoops(PsiFile file) {
        return PsiTreeUtil.findChildrenOfType(file, PsiLoopStatement.class)
                .stream()
                .toList();
    }

    private PsiElement findLoopKeyword(PsiLoopStatement loop) {
        return PsiTreeUtil.findChildOfType(loop, PsiKeyword.class);
    }
}
