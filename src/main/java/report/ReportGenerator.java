package report;

import com.google.gson.Gson;
import okhttp3.*;
import okhttp3.Headers.Builder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class ReportGenerator {

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

    public Builder authenticate(Builder headers) {
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
        return headers;
    }
    public Headers.Builder getHeaders() {
        Headers.Builder headersBuilder = new Headers.Builder();
        authenticate(headersBuilder);
        return headersBuilder;
    }
}
