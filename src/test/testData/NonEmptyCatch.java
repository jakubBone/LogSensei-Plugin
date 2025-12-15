
public class NonEmptyCatch {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NonEmptyCatch.class);

    public void withLog() {
        try {
            riskyOperation();
        } catch (Exception e) {
            log.error("Error", e);  // no action
        }
    }

    public void withPrint() {
        try {
            riskyOperation();
        } catch (Exception e) {
            System.out.println("An error occurred");  // no action
        }
    }

    private void riskyOperation()  {}
}