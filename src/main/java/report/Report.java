package report;

import java.util.List;

public class Report {
    private String project;
    private String application;
    private String sonarurl;
    private String sonarcomponent;
    private List<Issue> issues;
    private List<Hotspot> hotspots;

    public Report(String project, String application, String sonarurl, String sonarcomponent,
                  List<Issue> issues, List<Hotspot> hotspots) {
        this.project = project;
        this.application = application;
        this.sonarurl = sonarurl;
        this.sonarcomponent = sonarcomponent;
        this.issues = issues;
        this.hotspots = hotspots;
    }

    public String getproject() {
        return project;
    }

    public void setproject(String project) {
        this.project = project;
    }

    public String getApplicationName() {
        return application;
    }

    public void setApplicationName(String application) {
        this.application = application;
    }

    public String getSonarBaseURL() {
        return sonarurl;
    }

    public void setSonarBaseURL(String sonarurl) {
        this.sonarurl = sonarurl;
    }

    public String getSonarComponent() {
        return sonarcomponent;
    }

    public void setSonarComponent(String sonarcomponent) {
        this.sonarcomponent = sonarcomponent;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }
}