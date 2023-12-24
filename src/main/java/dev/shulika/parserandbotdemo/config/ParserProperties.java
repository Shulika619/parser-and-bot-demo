package dev.shulika.parserandbotdemo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "parser")
public class ParserProperties {

    @Value("${parser.site-name}")
    private String siteName;

    @Value("${parser.site-url}")
    private String siteUrl;

    @Value("${parser.task-cron}")
    private String taskCron;

    @Value("${parser.bot-name}")
    private String botName;

    @Value("${parser.bot-token}")
    private String botToken;

    @Value("${parser.admin-chat-id}")
    private Long adminChatId;

}
