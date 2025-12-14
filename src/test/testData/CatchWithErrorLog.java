
public class CatchWithErrorLog {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CatchWithErrorLog.class);

    public void method() {
        try {
            riskyOperation();
        } catch (Exception e) {
            log.error("Error", e);  // Log available - no action
        }
    }

    private void riskyOperation() throws Exception {}
}