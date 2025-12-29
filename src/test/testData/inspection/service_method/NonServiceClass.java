import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// No @Service annotation
public class NonServiceClass {

    private static final Logger log = LoggerFactory.getLogger(NonServiceClass.class);

    public void <weak_warning descr="LogSensei: Service method missing entry and exit logs">noAnyLogs</weak_warning>(){
        String name = "test";
    }

    public void publicMethod(){
        log.info("entry");
        String name = "test"; // no action
        log.info("exit");
    }

}