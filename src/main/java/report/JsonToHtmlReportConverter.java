package report;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/*Класс генерирующий из json html*/

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
        html.append("<script src='https://cdn.jsdelivr.net/npm/chart.js'></script>");
        html.append("<style>");
        html.append("table {width: 100%; border-collapse: collapse;}");
        html.append("th, td {border: 1px solid black; padding: 8px; text-align: left;}");
        html.append("th {background-color: #f2f2f2;}");
        html.append("</style>");
        html.append("</head><body>");

        html.append("<h1>SonarQube Vulnerability Report</h1>");
        html.append("<p>Project Name: ").append(jsonData.get("project")).append("</p>");
        html.append("<p>Application: ").append(jsonData.get("application")).append("</p>");
        if (jsonData.get("issues") != null) {
            List<Map<String, Object>> issuesList = (List<Map<String, Object>>) jsonData.get("issues");
            html.append("<h2>Detail of the Detected Vulnerabilities</h2>");
            html.append("<table><tr><th>Rule</th><th>Severity</th><th>Component</th><th>Line</th><th>Description</th><th>Message</th><th>Status</th></tr>");

            for (Map<String, Object> issueMap : issuesList) {
                html.append("<tr>");
                String rule = (String) issueMap.get("rule");
                String ruleUrl = (String) issueMap.get("ruleUrl");
                html.append("<td><a href=\"").append(ruleUrl).append("\">").append(rule).append("</a></td>");
                html.append("<td>").append(issueMap.get("severity")).append("</td>");
                html.append("<td>").append(issueMap.get("component")).append("</td>");
                html.append("<td>").append(issueMap.get("line")).append("</td>");
                html.append("<td>").append(issueMap.get("description")).append("</td>");
                html.append("<td>").append(issueMap.get("message")).append("</td>");
                html.append("<td>").append(issueMap.get("status")).append("</td>");
                html.append("</tr>");
            }

            html.append("<tr><td colspan=\"7\"><hr></td></tr>");

            if (jsonData.get("hotspots") != null) {
                List<Map<String, Object>> hotspotsList = (List<Map<String, Object>>) jsonData.get("hotspots");
                for (Map<String, Object> hotspotMap : hotspotsList) {
                    html.append("<tr>");
                    String hotspotKey = (String) hotspotMap.get("key");
                    String hotspotUrl = (String) hotspotMap.get("ruleUrl");
                    html.append("<td><a href=\"").append(hotspotUrl).append("\">").append(hotspotKey).append("</a></td>");
                    html.append("<td>").append(hotspotMap.get("message")).append("</td>");
                    html.append("<td>").append(hotspotMap.get("status")).append("</td>");
                    html.append("<td>").append(hotspotMap.get("component")).append("</td>");
                    html.append("<td>").append(hotspotMap.get("line")).append("</td>");
                    html.append("</tr>");
                }
            }

            html.append("</table>");
        } else {
            html.append("<p>No issue data available.</p>");
        }

        html.append("</body></html>");

        return html.toString();
    }
}
