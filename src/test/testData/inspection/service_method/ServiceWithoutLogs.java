import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServiceWithoutLogs {

    private static final Logger log = LoggerFactory.getLogger(ServiceWithoutLogs.class);

    public void <weak_warning descr="LogSensei: Service method missing entry and exit logs">noAnyLogs</weak_warning>(){
        String name = "test";
    }

    public void <weak_warning descr="LogSensei: Service method missing entry log">noEntryLog</weak_warning>(){
        String name = "test";
        log.info("...");
    }

    public void <weak_warning descr="LogSensei: Service method missing exit log">noExitLog</weak_warning>(){
        log.info("...");
        String name = "test";
    }
}