# sonar-report-tool

## Installation
  ```mvn clean package```
  
  jar file will be saved to **/target**

## Usage

```
Usage: sonar-report-tool [--help] [--application=<application>]
                         [--project=<project>]
                         [--save-report-html=<saveReportHtml>]
                         [--save-report-json=<saveReportJson>]
                         [--save-report-sarif=<saveReportSarif>]
                         [--sonarcomponent=<sonarcomponent>]
                         [--sonarpassword=<sonarpassword>]
                         [--sonartoken=<sonartoken>] [--sonarurl=<sonarurl>]
                         [--sonarusername=<sonarusername>]

Generate a vulnerability report from a SonarQube instance.
      --application=<application> name of the application, displayed in the header of the generated report
      --help                Display this help message.
      --project=<project>   name of the project, displayed in the header of the generated report
      --save-report-html=<saveReportHtml> Save the report data in HTML format. Set to target file name
      --save-report-json=<saveReportJson> Save the report data in JSON format. Set to target file name
      --save-report-sarif=<saveReportSarif> Save the report data in JSON format. Set to target file name
      --sonarcomponent=<sonarcomponent> id of the component to query from
      --sonarpassword=<sonarpassword> auth password
      --sonartoken=<sonartoken> auth token
      --sonarurl=<sonarurl> base URL of the SonarQube instance to query from
      --sonarusername=<sonarusername> auth username
```

## Example of html report

![image](https://github.com/DyadyaMops/sonar-report-tool/assets/115101419/1a318c3b-14a6-4681-890f-411ed8ea57dc)
