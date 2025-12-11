package com.jakubbone.logsensei.inspection;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Test;


public class CatchBlockInspectionTest extends LightJavaCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/inspection";
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(CatchBlockInspection.class);
    }

    @Test
    public void testEmptyCatchHighlighting(){
        myFixture.testHighlighting(false, false, false,"EmptyCatch.java");
    }
}
