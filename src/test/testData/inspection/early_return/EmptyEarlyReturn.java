public class EmptyEarlyReturn {

    public void emptyEarly(String var){
        if(var == null){
            <weak_warning descr="LogSensei: Early return missing log">return</weak_warning>; // action required
        }
    }

    public void emptyEarlyWithDefault(String var){
        if(var == null){
            return default; // no action
        }
    }

    public void emptyEarlyWithNoBrackets(String var){
        if(var == null) <weak_warning descr="LogSensei: Early return missing log">return</weak_warning>; // action required
    }

}