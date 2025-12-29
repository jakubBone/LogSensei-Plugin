import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatchWithLogs {
    private static final Logger log = LoggerFactory.getLogger(CatchWithLogs.class);

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