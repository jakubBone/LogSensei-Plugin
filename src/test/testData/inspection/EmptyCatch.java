package testData.inspection;

public class EmptyCatch {
    public void method() {
        try {
            riskyOperation();
        } <weak_warning descr="Catch block missing ERROR log">catch </weak_warning> (Exception e)  {
            // Empty block - action expected
        }
    }

    private void riskyOperation() throws Exception {
        throw new Exception("test");
    }
}