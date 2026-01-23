import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// No @Service, @Controller or @RestController annotation
public class NonSpringComponent {

    private static final Logger log = LoggerFactory.getLogger(NonSpringComponent.class);

    public void noAnyLogs(){
        String name = "test";
    }

    public void publicMethod(){
        log.info("entry");
        String name = "test"; // no action
        log.info("exit");
    }
}
