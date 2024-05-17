package report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class JsonToSarifConverter {

    private final String sonarurl;

    public JsonToSarifConverter(String sonarurl) {
        this.sonarurl = sonarurl;
    }

    public void convertJsonToSarif(String inputFile, String outputFile) {
        String version = getVersion();
        if (version == null) {
            System.err.println("Failed to get SonarQube version. Exiting.");
            return;
        }

        try (FileReader reader = new FileReader(inputFile)) {
            JsonObject jsonData = JsonParser.parseReader(reader).getAsJsonObject();

            JsonObject sarifData = new JsonObject();
            sarifData.addProperty("$schema", "https://schemastore.azurewebsites.net/schemas/json/sarif-2.1.0-rtm.6.json");
            sarifData.addProperty("version", "2.1.0");

            JsonArray runsArray = new JsonArray();
            JsonObject runObject = new JsonObject();

            JsonArray resultsArray = new JsonArray();
            JsonObject toolObject = new JsonObject();
            JsonObject driverObject = new JsonObject();
            driverObject.addProperty("name", "SonarQube");
            driverObject.addProperty("version", version);

            toolObject.add("driver", driverObject);
            runObject.add("tool", toolObject);
            runObject.add("results", resultsArray);

            sarifData.add("runs", runsArray);
            runsArray.add(runObject);

            JsonArray issuesArray = jsonData.getAsJsonArray("issues");
            for (int i = 0; i < issuesArray.size(); i++) {
                JsonObject issue = issuesArray.get(i).getAsJsonObject();
                String ruleId = issue.get("rule").getAsString();
                String severity = issue.get("severity").getAsString();
                String component = issue.get("component").getAsString();
                int line = issue.get("line").getAsInt();
                String description = issue.get("description").getAsString();
                String message = issue.get("message").getAsString();

                JsonObject resultObject = new JsonObject();
                resultObject.addProperty("ruleId", ruleId);

                JsonObject messageObject = new JsonObject();
                messageObject.addProperty("text", description + " " + message);
                resultObject.add("message", messageObject);

                JsonObject locationObject = new JsonObject();
                JsonObject physicalLocationObject = new JsonObject();
                JsonObject artifactLocationObject = new JsonObject();
                artifactLocationObject.addProperty("uri", "/sources/" + component);
                physicalLocationObject.add("artifactLocation", artifactLocationObject);

                JsonObject regionObject = new JsonObject();
                regionObject.addProperty("startLine", line);
                physicalLocationObject.add("region", regionObject);

                locationObject.add("physicalLocation", physicalLocationObject);
                JsonArray locationsArray = new JsonArray();
                locationsArray.add(locationObject);
                resultObject.add("locations", locationsArray);

                JsonObject propertiesObject = new JsonObject();
                propertiesObject.addProperty("severity", severity);
                resultObject.add("properties", propertiesObject);

                resultsArray.add(resultObject);
            }

            JsonArray hotspotsArray = jsonData.getAsJsonArray("hotspots");
            for (int i = 0; i < hotspotsArray.size(); i++) {
                JsonObject hotspot = hotspotsArray.get(i).getAsJsonObject();
                String severity = hotspot.get("status").getAsString();
                String component = hotspot.get("component").getAsString();
                int line = hotspot.get("line").getAsInt();
                String message = hotspot.get("message").getAsString();

                JsonObject resultObject = new JsonObject();
                resultObject.addProperty("ruleId", "hotspot");

                JsonObject messageObject = new JsonObject();
                messageObject.addProperty("text", message);
                resultObject.add("message", messageObject);

                JsonObject locationObject = new JsonObject();
                JsonObject physicalLocationObject = new JsonObject();
                JsonObject artifactLocationObject = new JsonObject();
                artifactLocationObject.addProperty("uri", "/sources/" + component);
                physicalLocationObject.add("artifactLocation", artifactLocationObject);

                JsonObject regionObject = new JsonObject();
                regionObject.addProperty("startLine", line);
                physicalLocationObject.add("region", regionObject);

                locationObject.add("physicalLocation", physicalLocationObject);
                JsonArray locationsArray = new JsonArray();
                locationsArray.add(locationObject);
                resultObject.add("locations", locationsArray);

                JsonObject propertiesObject = new JsonObject();
                propertiesObject.addProperty("severity", severity);
                resultObject.add("properties", propertiesObject);

                resultsArray.add(resultObject);
            }

            JsonArray artifactsArray = new JsonArray();
            runObject.add("artifacts", artifactsArray);
            for (int i = 0; i < issuesArray.size(); i++) {
                JsonObject issue = issuesArray.get(i).getAsJsonObject();
                String component = issue.get("component").getAsString();
                boolean artifactExists = false;

                for (int j = 0; j < artifactsArray.size(); j++) {
                    if (artifactsArray.get(j).getAsJsonObject().get("location").getAsJsonObject().get("uri").getAsString().equals("/" + component)) {
                        artifactExists = true;
                        break;
                    }
                }

                if (!artifactExists) {
                    JsonObject artifactObject = new JsonObject();
                    JsonObject locationObject = new JsonObject();
                    locationObject.addProperty("uri", "/" + component);
                    artifactObject.add("location", locationObject);
                    artifactsArray.add(artifactObject);
                }
            }

            for (int i = 0; i < hotspotsArray.size(); i++) {
                JsonObject hotspot = hotspotsArray.get(i).getAsJsonObject();
                String component = hotspot.get("component").getAsString();
                boolean artifactExists = false;

                for (int j = 0; j < artifactsArray.size(); j++) {
                    if (artifactsArray.get(j).getAsJsonObject().get("location").getAsJsonObject().get("uri").getAsString().equals("/" + component)) {
                        artifactExists = true;
                        break;
                    }
                }

                if (!artifactExists) {
                    JsonObject artifactObject = new JsonObject();
                    JsonObject locationObject = new JsonObject();
                    locationObject.addProperty("uri", "/" + component);
                    artifactObject.add("location", locationObject);
                    artifactsArray.add(artifactObject);
                }
            }

            try (FileWriter writer = new FileWriter(outputFile)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(sarifData, writer);
                System.out.println("SARIF report saved to " + outputFile);
            }
        } catch (IOException e) {
            System.err.println("Failed to read or write file: " + e.getMessage());
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
}
