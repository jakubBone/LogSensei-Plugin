import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugWarnLoggingLoop {

    private static final Logger log = LoggerFactory.getLogger(DebugWarnLoggingLoop.class);

    public void existingDebugLogInFor() {
        int limit = 10;
        for(int i = 0; i < 10; i++) {
            log.debug("..."); // no action
        }
    }

    public void warnInWhile() {
        int i = 0;
        while(i < 5) {
            log.warn("something");  // no action
            i++;
        }
    }
}