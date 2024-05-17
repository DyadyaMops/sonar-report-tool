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
}