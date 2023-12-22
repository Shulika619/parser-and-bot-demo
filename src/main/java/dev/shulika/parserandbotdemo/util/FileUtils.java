package dev.shulika.parserandbotdemo.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FileUtils {

    public static final String generateFilePath(String siteName) {

        LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss");
        String dateTime = formatter.format(currentDateTime);

        return String.format("%s/%s %s.csv", siteName, siteName, dateTime);
    }

    public static final List<String> getCategoriesFromFile() throws IOException {
        String filePath = "categories.txt";
        return Files.readAllLines(Paths.get(filePath));
    }

    public static final Document getHtmlDocument(String url, Integer timeout) throws IOException {
        return Jsoup
                .connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.3")
                .timeout(timeout)
                .get();
    }

    public static File findLastModifiedFile(String searchDir) {

        File dir = new File(searchDir);

        if (dir.isDirectory()) {
            Optional<File> opFile = Arrays.stream(dir.listFiles(File::isFile))
                    .max((f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));

            if (opFile.isPresent()) {
                return opFile.get();
            }
        }

        return null;
    }

}
