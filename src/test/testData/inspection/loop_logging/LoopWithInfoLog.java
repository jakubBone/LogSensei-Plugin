import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoopWithInfoLog {

    private static final Logger log = LoggerFactory.getLogger(LoopWithInfoLog.class);

    public void logInWhile() {
        int i = 0;
        while (i != 5) {
            log.<weak_warning descr="LogSensei: High-frequency logs detected in loop. Consider using DEBUG level.">info</weak_warning>("...");
            i++;
        }
    }

    public void logInDoWhile() {
        int i = 0;
        do {
            log.<weak_warning descr="LogSensei: High-frequency logs detected in loop. Consider using DEBUG level.">info</weak_warning>("...");
            i++;
        } while (i != 5);
    }

    public void logInForeach() {
        List<Integer> list = List.of(2, 4, 6);
        for (int n : list) {
            log.<weak_warning descr="LogSensei: High-frequency logs detected in loop. Consider using DEBUG level.">info</weak_warning>("...");

        }
    }

   public void logInFor() {
        int limit = 10;
        for(int i = 0; i < limit; i++) {
            log.<weak_warning descr="LogSensei: High-frequency logs detected in loop. Consider using DEBUG level.">info</weak_warning>("...");
        }
    }
}