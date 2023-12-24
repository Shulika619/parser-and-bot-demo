package dev.shulika.parserandbotdemo.telegram;

import dev.shulika.parserandbotdemo.config.ParserProperties;
import dev.shulika.parserandbotdemo.service.ParserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.shulika.parserandbotdemo.telegram.BotConst.*;
import static dev.shulika.parserandbotdemo.util.FileUtils.findLastModifiedFile;

@Component
@Slf4j
public class ResponseHandler {

    private MessageSender sender;
    private SilentSender silentSender;
    private Map<Long, String> usersAccess;

    private final ParserService parserService;
    private final String siteName;
    private final Long adminChatId;

    public ResponseHandler(ParserService parserService, ParserProperties parserProperties) {
        this.parserService = parserService;
        this.siteName = parserProperties.getSiteName();
        this.adminChatId = parserProperties.getAdminChatId();
    }

    public void init(MessageSender sender, SilentSender silentSender, DBContext db) {
        this.sender = sender;
        this.silentSender = silentSender;
        this.usersAccess = db.getMap(USERS_ACCESS);
    }

    public void replyToStart(Long chatId) {
        log.info("+++ IN ResponseHandler :: replyToStart :: /start");
        sendText(chatId, START_MSG);
    }

    public void replyToPrice(Long chatId) {
        log.info("+++ IN ResponseHandler :: replyToPrice :: /price");

        if (!userIsHasAccess(chatId)) {
            log.error("--- User with chatID-{} denied access", chatId);
            sendText(chatId, DENIED_ACCESS);
            return;
        }
        sendTextWithButtons(chatId);
    }

    public void replyToButtons(Long chatId, Update update) {
        log.info("+++ IN ResponseHandler :: replyToButtons");

        CallbackQuery query = update.getCallbackQuery();
        String queryData = query.getData();

        if (queryData.equals(siteName)) {
            sendDocument(chatId, siteName);
        }

    }

    public void replyToUserAddAccess(Long chatId, Message message) {
        log.info("+++ IN ResponseHandler :: replyToUserAddAccess :: ADD");

        String text = message.getText();
        String[] splitString = text.split(" ");
        Long addChatId = Long.valueOf(splitString[1]);
        String firstName = splitString[2];

        usersAccess.put(addChatId, firstName);
        sendText(chatId, "ADDED");
    }

    public void replyToUserDeleteAccess(Long chatId, Message message) {
        log.info("+++ IN ResponseHandler :: replyToUserDeleteAccess :: DELETE");

        String text = message.getText();
        String[] splitString = text.split(" ");
        Long deleteChatId = Long.valueOf(splitString[1]);

        usersAccess.remove(deleteChatId);
        sendText(chatId, "DELETED");
    }

    public void replyToUserListAccess(Long chatId) {
        log.info("+++ IN ResponseHandler :: replyToUserListAccess :: LIST");

        if (usersAccess.isEmpty()) {
            log.error("--- IN ResponseHandler :: replyToUserListAccess :: usersAccess :: IS EMPTY");
            sendText(chatId, "Empty");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        usersAccess.forEach((k, v) -> stringBuilder.append(k + ":" + v + "\n"));
        sendText(chatId, stringBuilder.toString());
    }

    @SneakyThrows
    public void replyToParse(Long chatId, Message message) {
        log.info("+++ IN ResponseHandler :: replyToParse :: Parse from Bot");

        String text = message.getText();
        String[] splitString = text.split(" ");
        String name = splitString[1];

        if (name.equals(siteName)) {
            sendText(chatId, "Parse started 1️⃣ " + siteName);
            parserService.start();
        }

    }

    private void sendText(Long chatId, String text) {
        SendMessage sendMessage = SendMessage.builder()
                .text(text)
                .chatId(chatId)
                .parseMode(ParseMode.MARKDOWNV2)
                .build();
        silentSender.execute(sendMessage);
    }

    private void sendTextWithButtons(Long chatId) {
        SendMessage sendMessage = SendMessage.builder()
                .text(SELECT_STORE)
                .chatId(chatId)
                .parseMode(ParseMode.MARKDOWNV2)
                .build();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(
                InlineKeyboardButton.builder().text(siteName).callbackData(siteName).build()
        ));
        inlineKeyboardMarkup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        silentSender.execute(sendMessage);
    }

    private void sendDocument(Long chatId, String siteName) {
        log.info("+++ IN ResponseHandler :: sendDocument :: {}", siteName);

        File filePath = findLastModifiedFile(siteName);

        if (filePath == null) {
            log.error("--- IN ResponseHandler :: sendDocument :: {} :: IS EMPTY", siteName);
            sendText(chatId, NO_FILE);
            return;
        }

        SendDocument sendDocument = SendDocument.builder()
                .chatId(chatId)
                .document(new InputFile(filePath))
                .build();

        try {
            sender.sendDocument(sendDocument);
        } catch (TelegramApiException e) {
            log.error("--- IN ResponseHandler :: sendDocument :: {} :: {}", siteName, e.getMessage());
        }

    }

    public boolean userIsHasAccess(Long chatId) {
        return usersAccess.containsKey(chatId) || adminChatId.equals(chatId);
    }

    public boolean userIsAdmin(Long chatId) {
        return adminChatId.equals(chatId);
    }

}
