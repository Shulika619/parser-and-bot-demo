package dev.shulika.parserandbotdemo.service;

import dev.shulika.parserandbotdemo.config.ParserProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.SocketTimeoutException;

@Service
@EnableAsync
@RequiredArgsConstructor
@Slf4j
public class ParserServiceImpl implements ParserService{

    private final ParserProperties parserProperties;

    @Async
    @Scheduled(cron = "${parser.task-cron}", zone = "Europe/Moscow")
    @Retryable(value = SocketTimeoutException.class, maxAttempts = 3, backoff = @Backoff(delay = 30000))
    @Override
    public void start() {
        log.info("+++ ParserServiceImpl :: start()");



    }

    @Recover
    private void recover(SocketTimeoutException e) {
        log.error("--- ParserServiceImpl :: recover()::All RETRY ended in failure");
        Thread.currentThread().interrupt();
    }

}
