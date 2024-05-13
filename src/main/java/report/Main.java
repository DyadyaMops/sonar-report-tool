package report;

import picocli.CommandLine;

import java.util.Map;

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

        // Создаем экземпляр ReportGenerator на основе переданных аргументов
        ReportGenerator reportGenerator = new ReportGenerator(
                project, application, branch, release, sonarurl, sonarusername,
                sonarpassword, sonarcomponent, sonartoken, saveReportJson
        );

        // Создаем экземпляр ExecuteRequest на основе ReportGenerator
        ExecuteRequest executeRequest = new ExecuteRequest(reportGenerator);

        // Выполняем поиск проблем (issues) с использованием ExecuteRequest
        Map<String, Object> issuesData = executeRequest.executeIssuesSearch(sonarurl, sonarcomponent);

        // Обработка результатов
        if (issuesData != null) {
            System.out.println("Issues retrieved successfully:");
            System.out.println(issuesData); // Вывод данных о проблемах в консоль или дальнейшая обработка
        } else {
            System.err.println("Failed to retrieve issues.");
        }
    }
}
