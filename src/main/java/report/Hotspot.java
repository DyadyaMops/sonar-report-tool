package report;

/*Класс определяющий структуру Hotspot*/

public class Hotspot {
    private String message;
    private String status;
    private String ruleUrl;
    private String component;
    private int line;
    private String key;


    public void setMessage(String message) {this.message = message;}

    public void setStatus(String status) {this.status = status;}

    public void setRuleUrl(String ruleUrl){this.ruleUrl = ruleUrl;}

    public void setComponent(String component) {this.component = component;}

    public void setLine(int line) {this.line = line;}

    public void setKey(String key) {this.key = key;}
}
