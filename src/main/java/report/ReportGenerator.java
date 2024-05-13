package report;

import com.google.gson.Gson;
import okhttp3.*;
import okhttp3.Headers.Builder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ReportGenerator {

    private String project;
    private String application;
    private String branch;
    private String release;
    private String sonarurl;
    private String sonarusername;
    private String sonarpassword;
    private String sonarcomponent;
    private String sonartoken;
    private String saveReportJson;

    public ReportGenerator(String project, String application, String branch, String release, String sonarurl, String sonarusername, String sonarpassword, String sonarcomponent, String sonartoken, String saveReportJson) {
        this.project = project;
        this.application = application;
        this.branch = branch;
        this.release = release;
        this.sonarurl = sonarurl;
        this.sonarusername = sonarusername;
        this.sonarpassword = sonarpassword;
        this.sonarcomponent = sonarcomponent;
        this.sonartoken = sonartoken;
        this.saveReportJson = saveReportJson;
    }

    private String encodeURIComponent(String sonarcomponent) {
        try {
            return URLEncoder.encode(sonarcomponent, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to encode URI component: " + e.getMessage(), e);
        }
    }
    public String getVersion() {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        String apiUrl = sonarurl + "/api/system/status";

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                VersionInfo versionInfo = gson.fromJson(responseBody, VersionInfo.class);
                return versionInfo.getVersion();
            } else {
                System.err.println("Failed to fetch SonarQube version. Status code: " + response.code());
            }
        } catch (IOException e) {
            System.err.println("Error occurred while getting SonarQube version: " + e.getMessage());
        }
        return null;
    }

    private static class VersionInfo {
        private String version;

        public String getVersion() {
            return version;
        }
    }

    // Метод для установки фильтров в зависимости от версии SonarQube
//    public String setFilters(String version, boolean noSecurityHotspot, boolean allBugs, String pullRequest, String branch, boolean fixMissingRule) {
//        String defaultIssuesFilter;
//        String defaultRulesFilter;
//        String issueStatuses;
//
//        if (noSecurityHotspot || version == null || version.matches("<7\\.3")) {
//            defaultIssuesFilter = "&types=VULNERABILITY";
//            defaultRulesFilter = "&types=VULNERABILITY";
//            issueStatuses = "OPEN,CONFIRMED,REOPENED";
//        } else if (version.matches("7\\.3 - 7\\.8")) {
//            defaultIssuesFilter = "&types=VULNERABILITY,SECURITY_HOTSPOT";
//            defaultRulesFilter = "&types=VULNERABILITY,SECURITY_HOTSPOT";
//            issueStatuses = "OPEN,CONFIRMED,REOPENED";
//        } else if (version.matches("7\\.8 - 7\\.9")) {
//            defaultIssuesFilter = "&types=VULNERABILITY,SECURITY_HOTSPOT";
//            defaultRulesFilter = "&types=VULNERABILITY,SECURITY_HOTSPOT";
//            issueStatuses = "OPEN,CONFIRMED,REOPENED,TO_REVIEW";
//        } else {
//            // version >= 8.0
//            defaultIssuesFilter = "&types=VULNERABILITY";
//            defaultRulesFilter = "&types=VULNERABILITY,SECURITY_HOTSPOT";
//            issueStatuses = "OPEN,CONFIRMED,REOPENED";
//        }
//
//        // Применяем фильтры в зависимости от условий
//        String filterIssue = defaultIssuesFilter;
//        String filterRule = defaultRulesFilter;
//        String issueStatus = issueStatuses;
//
//        return filterIssue, filterRule, issueStatus;
//    }

        public void authenticate(Headers.Builder headers) {
            if (sonarusername != null && sonarpassword != null) {
                // Form authentication with username/password
                String loginUrl = sonarurl + "/api/authentication/login";
                String requestBody = "login=" + encodeURIComponent(sonarusername) +
                        "&password=" + encodeURIComponent(sonarpassword);

                RequestBody body = RequestBody.create(
                        MediaType.parse("application/x-www-form-urlencoded"),
                        requestBody
                );

                Request request = new Request.Builder()
                        .url(loginUrl)
                        .post(body)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                OkHttpClient client = new OkHttpClient();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String cookie = response.header("Set-Cookie");
                        if (cookie != null) {
                            String sessionCookie = cookie.split(";")[0];
                            headers.add("Cookie", sessionCookie);
                        }
                    } else {
                        System.err.println("Failed to authenticate with SonarQube. Status code: " + response.code());
                    }
                } catch (IOException e) {
                    System.err.println("Error occurred during authentication: " + e.getMessage());
                }
            } else if (sonartoken != null) {
                // Basic authentication with user token
                String authToken = sonartoken + ":";
                String base64Token = Base64.getEncoder().encodeToString(authToken.getBytes(StandardCharsets.UTF_8));
                headers.add("Authorization", "Basic " + base64Token);
            }
        }


}
