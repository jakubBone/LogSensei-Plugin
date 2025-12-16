import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NonEmptyEarlyReturn {

private static final Logger log = LoggerFactory.getLogger(NonEmptyEarlyReturn.class);

    public void withLog(String var) {
        if(var == null){
            log.warn("Early return");  // no action
        }
    }

    public void withPrint(String var) {
        if(var == null){
            System.out.println("Early return");  // no action
        }
    }

    public void withNoBrackets(String var) {
        if(var == null) log.warn("Early return");
    }
}