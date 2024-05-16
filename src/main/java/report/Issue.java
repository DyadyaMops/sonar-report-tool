package report;

public class Issue {
    private String rule;
    private String severity;
    private String status;
    private String component;
    private int line;
    private String description;
    private String message;
    private String key;

    public void setRule(String rule) {
        this.rule = rule;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setKey(String key) {
        this.key = key;
    }
}