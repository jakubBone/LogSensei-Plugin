import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerWithLogs {

    private static final Logger log = LoggerFactory.getLogger(ControllerWithLogs.class);

    public void publicMethod(){
        log.info("entry");
        String name = "test"; // no action
        log.info("exit");
    }

    private void privMethod() {
        String name = "test"; // no action
    }
}
