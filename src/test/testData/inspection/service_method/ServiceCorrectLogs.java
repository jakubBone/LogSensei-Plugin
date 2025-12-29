import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServiceCorrectLogs {

    private static final Logger log = LoggerFactory.getLogger(ServiceMissingLogs.class);

    public void public(){
        log.info("entry");
        String name = "test"; // no action
        log.info("exit");
    }

    private void priv() {
        String name = "test"; // no action
    }
}