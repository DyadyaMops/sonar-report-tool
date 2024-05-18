package report;

import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        // Создаем экземпляр команды report.HelpCommand для вывода справочной информации
        Commands commands = new Commands();

        // Парсим аргументы командной строки
        CommandLine commandLine = new CommandLine(commands);

        // Парсим аргументы с учетом стандартных опций для вывода справочной информации
        commandLine.parseWithHandler(new CommandLine.RunLast().useOut(System.out), args);

        if (commandLine.isUsageHelpRequested()) {
            commandLine.usage(System.out);
            return; // Завершаем выполнение, не выполняя других действий
        }

        // Извлекаем аргументы из commands
        String project = commands.project;
        String application = commands.application;
        String branch = commands.branch;
        String release = commands.release;
        String sonarurl = commands.sonarurl;
        String sonarusername = commands.sonarusername;
        String sonarpassword = commands.sonarpassword;
        String sonarcomponent = commands.sonarcomponent;
        String sonartoken = commands.sonartoken;
        String saveReportJson = commands.saveReportJson;
        String saveReportSarif = commands.saveReportSarif;
        String saveReportHtml = commands.saveReportHtml;

        // Создаем экземпляр ReportGenerator на основе переданных аргументов
        ReportGenerator reportGenerator = new ReportGenerator(
                project, application, branch, release, sonarurl, sonarusername,
                sonarpassword, sonarcomponent, sonartoken, saveReportJson
        );

        // Создаем экземпляр ExecuteRequest на основе ReportGenerator
        ExecuteRequest executeRequest = new ExecuteRequest(reportGenerator);

        // Выполняем поиск проблем (issues) с использованием ExecuteRequest
        Map<String, Object> issuesData = executeRequest.executeIssuesSearch(sonarurl);
        Map<String, Object> hotspotsData = executeRequest.executeHotspotsSearch(sonarurl);

        List<Issue> issuesList = new ArrayList<>();
        if (issuesData != null && issuesData.containsKey("issues")) {
            Map<String, Object> issuesMap = (Map<String, Object>) issuesData.get("issues");
            for (Map.Entry<String, Object> entry : issuesMap.entrySet()) {
                Map<String, Object> issueData = (Map<String, Object>) entry.getValue();
                Issue issue = new Issue();
                issue.setRule((String) issueData.get("rule"));
                issue.setSeverity((String) issueData.get("severity"));
                issue.setStatus((String) issueData.get("status"));
                issue.setComponent((String) issueData.get("component"));
                issue.setLine((int) issueData.get("line"));
                issue.setDescription((String) issueData.get("description"));
                issue.setMessage((String) issueData.get("message"));
                issue.setKey(entry.getKey());
                issuesList.add(issue);

                //TODO добавить обработку summary по issues и убрать двойной вывод о генерации файлов в консоль
            }
        }

        // Преобразуем данные о hotspots в список Hotspot
        List<Hotspot> hotspotsList = new ArrayList<>();
        if (hotspotsData != null && hotspotsData.containsKey("hotspots")) {
            Map<String, Object> hotspotsMap = (Map<String, Object>) hotspotsData.get("hotspots");
            for (Map.Entry<String, Object> entry : hotspotsMap.entrySet()) {
                Map<String, Object> hotspotData = (Map<String, Object>) entry.getValue();
                Hotspot hotspot = new Hotspot();
                hotspot.setMessage((String) hotspotData.get("message"));
                hotspot.setStatus((String) hotspotData.get("status"));
                hotspot.setComponent((String) hotspotData.get("component"));
                hotspot.setLine((int) hotspotData.get("line"));
                hotspot.setKey(entry.getKey());
                hotspotsList.add(hotspot);
            }
        }

        // Обработка результатов
        if (issuesData != null || hotspotsData != null) {
            System.out.println("Issues and hotspots retrieved successfully:");
            // Вывод данных о проблемах и hotspots в консоль или дальнейшая обработка
            try {
                String issuesFilePath = saveReportJson;
                reportGenerator.generateJsonReport(project, application, sonarurl, sonarcomponent, issuesList, hotspotsList, issuesFilePath);
                System.out.println("Issues and hotspots report saved to: " + issuesFilePath);

                // Проверка наличия флагов для сохранения в формате SARIF
                if (saveReportJson != null && !saveReportJson.isEmpty() && saveReportSarif != null && !saveReportSarif.isEmpty()) {
                    JsonToSarifConverter jsonToSarifConverter = new JsonToSarifConverter(sonarurl);
                    jsonToSarifConverter.convertJsonToSarif(saveReportJson, saveReportSarif);
                    System.out.println("SARIF report saved to: " + saveReportSarif);
                }

                // Проверка наличия флага для сохранения HTML отчета
                if (saveReportHtml != null && !saveReportHtml.isEmpty()) {
                    JsonToHtmlReportConverter jsonToHtmlReportConverter = new JsonToHtmlReportConverter();
                    jsonToHtmlReportConverter.convertJsonToHtml(saveReportJson, saveReportHtml);
                    System.out.println("HTML report saved to: " + saveReportHtml);
                }
            } catch (IOException e) {
                System.err.println("Failed to save issues and hotspots report: " + e.getMessage());
            }
        } else {
            System.err.println("Failed to retrieve issues and hotspots.");
        }
    }
}

