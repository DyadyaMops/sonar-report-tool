package report;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.List;
public class JsonToHtmlReportConverter {

    public void convertJsonToHtml(String jsonFilePath, String htmlFilePath) {
        try (FileReader reader = new FileReader(jsonFilePath)) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> jsonData = gson.fromJson(reader, type);

            String htmlReport = generateHtmlReport(jsonData);

            try (FileWriter writer = new FileWriter(htmlFilePath)) {
                writer.write(htmlReport);
            }

            System.out.println("HTML report saved to: " + htmlFilePath);

        } catch (IOException e) {
            System.err.println("Failed to convert JSON to HTML: " + e.getMessage());
        }
    }

    private String generateHtmlReport(Map<String, Object> jsonData) {
        StringBuilder html = new StringBuilder();

        html.append("<html><head><title>SonarQube Vulnerability Report</title>");
        html.append("<style>");
        html.append("table {width: 100%; border-collapse: collapse;}");
        html.append("th, td {border: 1px solid black; padding: 8px; text-align: left;}");
        html.append("th {background-color: #f2f2f2;}");
        html.append("</style>");
        html.append("</head><body>");

        html.append("<h1>SonarQube Vulnerability Report</h1>");
        html.append("<p>Project Name: ").append(jsonData.get("project")).append("</p>");
        html.append("<p>Application: ").append(jsonData.get("application")).append("</p>");
        html.append("<table><tr><th>Severity</th><th>Number of Issues</th></tr>");
        if (jsonData.get("summary") != null) {
            Map<String, Integer> summary = (Map<String, Integer>) jsonData.get("summary");

            html.append("<h2>Summary of the Detected Vulnerabilities</h2>");
            html.append("<table><tr><th>Severity</th><th>Number of Issues</th></tr>");

            summary.forEach((severity, count) -> {
                html.append("<tr><td>").append(severity).append("</td><td>").append(count).append("</td></tr>");
            });

            html.append("</table>");
        } else {
            html.append("<p>No summary data available.</p>");
        }

        // Add details of the detected vulnerabilities
        if (jsonData.get("issues") != null) {
            List<Map<String, Object>> issues = (List<Map<String, Object>>) jsonData.get("issues");
            html.append("<h2>Detail of the Detected Vulnerabilities</h2>");
            html.append("<table><tr><th>Rule</th><th>Severity</th><th>Component</th><th>Line</th><th>Description</th><th>Message</th><th>Status</th></tr>");

            for (Map<String, Object> issueMap : issues) {
                html.append("<tr>");
                html.append("<td>").append(issueMap.get("rule")).append("</td>");
                html.append("<td>").append(issueMap.get("severity")).append("</td>");
                html.append("<td>").append(issueMap.get("component")).append("</td>");
                html.append("<td>").append(issueMap.get("line")).append("</td>");
                html.append("<td>").append(issueMap.get("description")).append("</td>");
                html.append("<td>").append(issueMap.get("message")).append("</td>");
                html.append("<td>").append(issueMap.get("status")).append("</td>");
                html.append("</tr>");
            };

            html.append("</table>");
        } else {
            html.append("<p>No issue data available.</p>");
        }

        html.append("</body></html>");

        return html.toString();
    }
}