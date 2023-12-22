package dev.shulika.parserandbotdemo.service;

import dev.shulika.parserandbotdemo.config.ParserProperties;
import lombok.RequiredArgsConstructor;
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
public class ParserServiceImpl implements ParserService{

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
    public void start() throws IOException {
        log.info("+++ ParserServiceImpl :: start()");

        List<String> categories = getCategoriesFromFile();
        System.out.println(categories);
        File csvFile = new File(generateFilePath(siteName));

//        try (PrintWriter printWriter = new PrintWriter(csvFile, "Cp1251")) {    // or StandardCharsets.UTF_8
//
//            for (String category : CATEGORIES_BIT) {
//                log.info("+++ START PARSE CATEGORY {}{}", URL_BIT, category);
//
//                int pageCount = 1;
//                String parseUrl = URL_BIT + category + pageCount;
//                boolean isLastPage;
//
//                do {
//                    log.info("~~~ Started write to file {}", parseUrl);
//
//                    Document doc = getHtmlDocument(parseUrl, 30000);
//
//                    Elements divs = doc.select("div.product-about");
//                    for (Element div : divs) {
//                        List<String> row = new ArrayList<>();
//
//                        String name = div.select("div.name > a").first().text().replace("\"", "");
//                        String price = div.select("div.price").first().text();
//                        String link = div.select("div.name > a").first().attr("href");
//                        String stock = div.select("div.oct-cat-stock > span").first().text();
//
//                        row.add("\"" + name + "\"");
//                        row.add("\"" + price + "\"");
//                        row.add("\"" + link + "\"");
//                        row.add("\"" + stock + "\"");
//
//                        printWriter.println(String.join(";", row));     // default delimiter "," let's use ";"
//                        if (printWriter.checkError())
//                            log.error("--- Some error occurred while writing to the file :: {}", parseUrl);
//                    }
//
//                    Elements liElements = doc.select("div.pagination > ul > li");
//                    if (liElements.isEmpty()) {
//                        break;
//                    }
//
//                    isLastPage = liElements.last().hasClass("active");
//                    pageCount++;
//                    parseUrl = URL_BIT + category + pageCount;
//                    Thread.sleep(ThreadLocalRandom.current().nextInt(5000, 8001));
//
//                } while (!isLastPage);
//
////                printWriter.write('\ufeff');  // if UTF-8 ???
//                printWriter.println("; ; ;");
//                log.info("+++ FINISHED PARSE All PAGES :: CATEGORY {}{}", URL_BIT, category);
//
//            }
//
//            log.info("=== FINISHED PARSE ALL CATEGORIES :: {}", NAME_BIT);
//
//        }

    }

    @Recover
    private void recover(SocketTimeoutException e) {
        log.error("--- ParserServiceImpl :: recover()::All RETRY ended in failure");
        Thread.currentThread().interrupt();
    }

}
