package dev.shulika.parserandbotdemo.exception;

import dev.shulika.parserandbotdemo.config.ParserProperties;
import dev.shulika.parserandbotdemo.telegram.Bot;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Slf4j
public class ExceptionHandler {

    private final Bot bot;
    private final Long adminChatId;

    public ExceptionHandler(Bot bot, ParserProperties parserProperties) {
        this.bot = bot;
        this.adminChatId = parserProperties.getAdminChatId();
    }

    @AfterThrowing(pointcut = "execution(* dev.shulika.parserandbotdemo..*.*(..))", throwing = "ex")
    public void handleError(Exception ex) {
        log.error("--- ExceptionHandler :: {}", ex.getMessage());
        bot.silent().send("⚠️ Exception in program ⚠️ \n\n" + ex.getMessage(), adminChatId);
    }

}
