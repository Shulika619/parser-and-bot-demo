package dev.shulika.parserandbotdemo.service;

import dev.shulika.parserandbotdemo.config.ParserProperties;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static dev.shulika.parserandbotdemo.util.FileUtils.*;

@Service
@EnableAsync
@Slf4j
public class ParserServiceImpl implements ParserService {

    private final String siteName;
    private final String siteUrl;

    public ParserServiceImpl(ParserProperties parserProperties) {
        this.siteName = parserProperties.getSiteName();
        this.siteUrl = parserProperties.getSiteUrl();
    }

    @Async
    @Scheduled(cron = "${parser.task-cron}", zone = "Europe/Moscow")
    @Retryable(value = SocketTimeoutException.class, maxAttempts = 3, backoff = @Backoff(delay = 30000))
    @Override
    public void start() throws IOException, InterruptedException {
        log.info("+++ ParserServiceImpl :: start()");

        List<String> categories = getCategoriesFromFile();
        File csvFile = generateFilePath(siteName);

        try (PrintWriter printWriter = new PrintWriter(csvFile, "Cp1251")) {    // or StandardCharsets.UTF_8

            for (String category : categories) {
                log.info("+++ START PARSE CATEGORY {}{}", siteUrl, category);

                String parseUrl = siteUrl + category;
                Elements nextElements;

                do {
                    log.info("~~~ Started write to file {}", parseUrl);

                    Document doc = getHtmlDocument(parseUrl, 30000);

                    Elements divs = doc.select("div.product-container");
                    for (Element div : divs) {
                        List<String> row = new ArrayList<>();

                        String name = div.select("div.right-block > h5 > a").first().text().replace("\"", "");
                        String price = div.select("div.right-block > div.content_price > span").first().text();
                        String link = div.select("div.right-block > h5 > a").first().attr("href");
                        Elements spanStock = div.select("div.right-block > span > span");
                        String stock = spanStock.isEmpty() ? "-" : spanStock.first().text();

                        row.add("\"" + name + "\"");
                        row.add("\"" + price + "\"");
                        row.add("\"" + link + "\"");
                        row.add("\"" + stock + "\"");

                        printWriter.println(String.join(";", row));     // default delimiter "," let's use ";"
                        if (printWriter.checkError())
                            log.error("--- Some error occurred while writing to the file :: {}", parseUrl);
                    }

                    nextElements = doc.select("li.pagination_next > a");
                    if (nextElements.isEmpty()) {
                        break;
                    }

                    parseUrl = siteUrl + nextElements.first().attr("href");
                    System.out.println("---- NEXT = " + parseUrl);
                    Thread.sleep(ThreadLocalRandom.current().nextInt(5000, 7001));

                } while (!nextElements.isEmpty());

//                printWriter.write('\ufeff');  // if UTF-8 ???
                printWriter.println("; ; ;");
                log.info("+++ FINISHED PARSE All PAGES :: CATEGORY {}{}", siteUrl, category);

            }

            log.info("=== FINISHED PARSE ALL CATEGORIES :: {}", siteName);

        }

    }

    @Recover
    private void recover(SocketTimeoutException e) {
        log.error("--- ParserServiceImpl :: recover()::All RETRY ended in failure");
        Thread.currentThread().interrupt();
    }

}
