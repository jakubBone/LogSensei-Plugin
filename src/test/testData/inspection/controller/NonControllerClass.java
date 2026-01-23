import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// No @RestController or @Controller annotation
public class NonControllerClass {

    private static final Logger log = LoggerFactory.getLogger(NonControllerClass.class);

    public void noAnyLogs(){
        String name = "test";
    }

    public void publicMethod(){
        log.info("entry");
        String name = "test"; // no action
        log.info("exit");
    }
}
