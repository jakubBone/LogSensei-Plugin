public class EarlyReturn {

    public void early(String var){
        if(var == null){
            <weak_warning descr="LogSensei: Early return missing log">return</weak_warning>; // action required
        }
    }}