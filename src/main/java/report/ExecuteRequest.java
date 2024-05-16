package report;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecuteRequest {

    private final ReportGenerator reportGenerator;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public String project;
    public String application;
    public String branch;
    public String release;
    public String sonarurl;
    public String sonarusername;
    public String sonarpassword;
    public String sonarcomponent;
    public String sonartoken;
    public String saveReportJson;


    public ExecuteRequest(ReportGenerator reportGenerator) {
        this.reportGenerator = reportGenerator;
        this.project = reportGenerator.project;
        this.application = reportGenerator.application;
        this.branch = reportGenerator.branch;
        this.release = reportGenerator.release;
        this.sonarurl = reportGenerator.sonarurl;
        this.sonarusername = reportGenerator.sonarusername;
        this.sonarpassword = reportGenerator.sonarpassword;
        this.sonarcomponent = reportGenerator.sonarcomponent;
        this.sonartoken = reportGenerator.sonartoken;
        this.saveReportJson = reportGenerator.saveReportJson;
    }


    public Map<String, Object> executeIssuesSearch(String sonarurl) {
        Map<String, Object> data = new HashMap<>();
        data.put("issues", new HashMap<String, Object>());
        data.put("rules", new HashMap<String, Object>());
        Headers.Builder headers = new Headers.Builder();

        reportGenerator.authenticate(headers);

        int pageSize = 500;
        int maxResults = 10000;
        int maxPage = maxResults / pageSize;
        int page = 1;
        int nbResults;

        do {
            try {
                String issuesUrl = sonarurl + String.format("/api/issues/search?componentKeys=%s&ps=%d&p=%d", sonarcomponent, pageSize, page);
                Request request = new Request.Builder()
                        .url(issuesUrl)
                        .get()
                        .headers(reportGenerator.getHeaders().build())
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    System.err.println("Failed to get issues. Status code: " + response.code());
                    return null;
                }

                String responseBody = response.body().string();
                IssuesSearchResult issuesSearchResult = gson.fromJson(responseBody, IssuesSearchResult.class);
                nbResults = issuesSearchResult.issues.size();

                issuesSearchResult.issues.forEach(issue -> {
                    Map<String, Object> rule = (Map<String, Object>) ((Map<String, Object>) data.get("rules")).get(issue.rule);
                    String message = rule != null ? (String) rule.get("name") : "/";
                    ((Map<String, Object>) data.get("issues")).put(issue.key, Map.of(
                            "rule", issue.rule,
                            "severity", issue.severity != null ? issue.severity : rule.get("severity"),
                            "status", issue.status,
                            "link", issueLink(data, issue),
                            "component", issue.component.split(":")[1], // Получить только имя файла без пути
                            "line", issue.line,
                            "description", message,
                            "message", issue.message,
                            "key", issue.key
                    ));
                });

                page++;
            } catch (IOException e) {
                System.err.println("Error occurred during API request: " + e.getMessage());
                return null;
            }
        } while (nbResults == pageSize && page <= maxPage);

        return data;
    }

    private String issueLink(Map<String, Object> data, IssuesSearchResult.Issue issue) {
        // Реализация логики для формирования ссылки на проблему (issue)
        return ""; // Заменить на логику формирования ссылки
    }

    private static class IssuesSearchResult {
        List<Issue> issues;

        static class Issue {
            String rule;
            String severity;
            String status;
            String component;
            int line;
            String message;
            String key;
        }
    }


    public Map<String, Object> executeHotspotsSearch(String sonarurl) {
        Map<String, Object> data = new HashMap<>();
        data.put("hotspots", new HashMap<String, Object>());
        Headers.Builder headers = new Headers.Builder();

        reportGenerator.authenticate(headers);

        int pageSize = 500;
        int maxResults = 10000;
        int maxPage = maxResults / pageSize;
        int page = 1;
        int nbResults;

        do {
            try {
                String hotspotsUrl = sonarurl + String.format("/api/hotspots/search?projectKey=%s&ps=%d&p=%d", sonarcomponent, pageSize, page);
                Request request = new Request.Builder()
                        .url(hotspotsUrl)
                        .get()
                        .headers(reportGenerator.getHeaders().build())
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    System.err.println("Failed to get hotspots. Status code: " + response.code());
                    return null;
                }

                String responseBody = response.body().string();
                HotspotsSearchResult hotspotsSearchResult = gson.fromJson(responseBody, HotspotsSearchResult.class);
                nbResults = hotspotsSearchResult.hotspots.size();

                hotspotsSearchResult.hotspots.forEach(hotspot -> {
                    Map<String, Object> hotspotData = new HashMap<>();
                    hotspotData.put("message", hotspot.message);
                    hotspotData.put("status", hotspot.status);
                    hotspotData.put("component", hotspot.component.split(":")[1]);
                    hotspotData.put("line", hotspot.line);

                    ((Map<String, Object>) data.get("hotspots")).put(hotspot.key, hotspotData);
                });

                page++;
            } catch (IOException e) {
                System.err.println("Error occurred during hotspot API request: " + e.getMessage());
                return null;
            }
        } while (nbResults == pageSize && page <= maxPage);

        return data;
    }

    private static class HotspotsSearchResult {
        List<Hotspot> hotspots;

        static class Hotspot {
            String key;
            String message;
            String status;
            String component;
            int line;
        }
    }

}
