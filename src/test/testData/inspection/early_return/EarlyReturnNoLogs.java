public class EarlyReturnNoLogs {

    public void emptyEarly(String var){
        if(var == null){
            <weak_warning descr="LogSensei: Early return missing log">return</weak_warning>; // action required
        }
    }

    public String emptyEarlyWithDefault(String var){
        if(var == null){
            return "var is null"; // no action
        }
        return var;
    }

    public void emptyEarlyWithNoBrackets(String var){
        if(var == null) <weak_warning descr="LogSensei: Early return missing log">return</weak_warning>; // action required
    }

}