
public class EmptyCatch {
    public void emptyBlock() {
        try {
            riskyOperation();
        } <weak_warning descr="LogSensei: Catch block missing ERROR log">catch</weak_warning> (Exception e)  {
            // Empty block - action expected
        }
    }

    public void withException() {
        try {
            riskyOperation();
        } <weak_warning descr="LogSensei: Catch block missing ERROR log">catch</weak_warning> (Exception e){
            throw new RuntimeException("IO failed", e); // Empty block - action expected
        }
    }

    private void riskyOperation() throws Exception {
        throw new Exception("test");
    }
}