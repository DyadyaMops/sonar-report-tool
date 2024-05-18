package report;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "sonar-report-tool", description = "Generate a vulnerability report from a SonarQube instance.")
public class Commands implements Runnable {
    @Option(names = "--project", description = "name of the project, displayed in the header of the generated report")
    public String project;

    @Option(names = "--application", description = "name of the application, displayed in the header of the generated report")
    public String application;

    @Option(names = "--release", description = "name of the release, displayed in the header of the generated report")
    public String release;

    @Option(names = "--branch", description = "Branch in Sonarqube that we want to get the issues for")
    public String branch;

    @Option(names = "--sonarurl", description = "base URL of the SonarQube instance to query from")
    public String sonarurl;

    @Option(names = "--sonarcomponent", description = "id of the component to query from")
    public String sonarcomponent;

    @Option(names = "--sonarusername", description = "auth username")
    public String sonarusername;

    @Option(names = "--sonarpassword", description = "auth password")
    public String sonarpassword;

    @Option(names = "--sonartoken", description = "auth token")
    public String sonartoken;

    @Option(names = "--save-report-json", description = "Save the report data in JSON format. Set to target file name")
    public String saveReportJson;

    @Option(names = "--save-report-sarif", description = "Save the report data in JSON format. Set to target file name")
    public String saveReportSarif;
    @Option(names = "--save-report-html", description = "Save the report data in HTML format. Set to target file name")
    public String saveReportHtml;

    @Option(names = "--help", usageHelp = true, description = "Display this help message.")
    public boolean helpRequested;

    @Override
    public void run() {
        if (helpRequested) {
            CommandLine.usage(this, System.out);
        }
    }
}