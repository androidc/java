Напиши программу на java, которая получает на вход файл с списком ссылок на веб-страницы, проходит по ним и возвращает два файла, в первом файле список страниц, которые вернули код 200, в другом файле список страниц, которые вернули код 404. Программа должна работать в 10 потоков

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
public class WebPageChecker {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java WebPageChecker <input_file>");
            System.exit(1);
        }
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<String> urls = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {
            String line;
            while ((line = reader.readLine()) != null) {
                urls.add(line);
            }
        }
        List<Future<String>> tasks = new ArrayList<>();
        for (String url : urls) {
            tasks.add(executor.submit(new WebPageCheckerTask(url)));
        }
        List<String> successUrls = new ArrayList<>();
        List<String> errorUrls = new ArrayList<>();
        for (Future<String> task : tasks) {
            String result = task.get();
            if (result.contains("200")) {
                successUrls.add(result.substring(0, result.indexOf(",")));
            } else if (result.contains("404")) {
                errorUrls.add(result.substring(0, result.indexOf(",")));
            }
        }
        writeUrlsToFile("success_urls.txt", successUrls);
        writeUrlsToFile("error_urls.txt", errorUrls);
        executor.shutdown();
    }
    private static void writeUrlsToFile(String filename, List<String> urls) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String url : urls) {
                writer.write(url);
                writer.newLine();
            }
        }
    }
    private static class WebPageCheckerTask implements Callable<String> {
        private final String url;
        public WebPageCheckerTask(String url) {
            this.url = url;
        }
        @Override
        public String call() throws Exception {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return url + "," + responseCode;
        }
    }
}

Основной метод main считывает аргумент командной строки - имя файла со списком ссылок на веб-страницы. Далее он создает пул из 10 потоков и для каждой страницы запускает задачу WebPageCheckerTask, которая проверяет код ответа сервера и возвращает строку вида "url,code". Результаты задач сохраняются в список tasks, который затем обрабатывается в цикле. Если код ответа равен 200, то URL страницы добавляется в список successUrls, а если 404, то в список errorUrls. Наконец, методы writeUrlsToFile используются для сохранения списков URL в соответствующие файлы.