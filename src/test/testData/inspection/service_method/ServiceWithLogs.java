import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServiceWithLogs {

    private static final Logger log = LoggerFactory.getLogger(ServiceWithLogs.class);

    public void publicMethod(){
        log.info("entry");
        String name = "test"; // no action
        log.info("exit");
    }

    private void privMethod() {
        String name = "test"; // no action
    }
}