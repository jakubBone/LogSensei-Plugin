import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServiceNoLogs {

    private static final Logger log = LoggerFactory.getLogger(ServiceNoLogs.class);

    public void <weak_warning descr="LogSensei: Service method missing entry and exit logs">noAnyLogs</weak_warning>(){
        String name = "test";
    }

    public void <weak_warning descr="LogSensei: Service method missing entry log">noEntryLog</weak_warning>(){
        String name = "test";
        log.info("exit");
    }

    public void <weak_warning descr="LogSensei: Service method missing exit log">noExitLog</weak_warning>(){
        log.info("entry");
        String name = "test";
    }

    public void <weak_warning descr="LogSensei: Service method missing entry and exit logs">empty</weak_warning>(){
    }

    public void <weak_warning descr="LogSensei: Service method missing exit log">withException</weak_warning>(){
        log.info("entry");
        throw new RuntimeException("Error");
    }
}