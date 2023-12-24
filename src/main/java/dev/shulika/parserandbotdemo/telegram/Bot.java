package dev.shulika.parserandbotdemo.telegram;

import dev.shulika.parserandbotdemo.config.ParserProperties;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.BiConsumer;

import static dev.shulika.parserandbotdemo.telegram.BotConst.*;
import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class Bot extends AbilityBot {

    private final ResponseHandler responseHandler;
    private final Long adminChatId;

    public Bot(ParserProperties parserProperties, ResponseHandler responseHandler) {
        super(parserProperties.getBotToken(), parserProperties.getBotName());
        this.adminChatId = parserProperties.getAdminChatId();
        this.responseHandler = responseHandler;
        responseHandler.init(sender, silent, db);
    }

    @Override
    public long creatorId() {
        return adminChatId;
    }

    public Ability startBot() {
        return Ability.builder()
                .name(START_COMMAND)
                .info("Starts the bot")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> responseHandler.replyToStart(ctx.chatId()))
                .build();
    }

    public Ability price() {
        return Ability.builder()
                .name(PRICE_COMMAND)
                .info("Ger file with prices")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> responseHandler.replyToPrice(ctx.chatId()))
                .build();
    }

    public Reply replyToButtons() {
        BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) -> responseHandler.replyToButtons(getChatId(upd), upd);
        return Reply.of(action, Flag.CALLBACK_QUERY, upd -> responseHandler.userIsHasAccess(getChatId(upd)));
    }

    public Reply replyUserAdd() {
        BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) -> responseHandler.replyToUserAddAccess(getChatId(upd), upd.getMessage());
        return Reply.of(action, Flag.TEXT, upd -> responseHandler.userIsAdmin(getChatId(upd)), upd -> hasMessageWith(upd, USER_ADD_COMMAND));
    }

    public Reply replyUserDelete() {
        BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) -> responseHandler.replyToUserDeleteAccess(getChatId(upd), upd.getMessage());
        return Reply.of(action, Flag.TEXT, upd -> responseHandler.userIsAdmin(getChatId(upd)), upd -> hasMessageWith(upd, USER_DELETE_COMMAND));
    }

    public Reply replyUserList() {
        BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) -> responseHandler.replyToUserListAccess(getChatId(upd));
        return Reply.of(action, Flag.TEXT, upd -> responseHandler.userIsAdmin(getChatId(upd)), upd -> hasMessageWith(upd, USER_LIST_COMMAND));
    }

    public Reply replyParse() {
        BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) -> responseHandler.replyToParse(getChatId(upd), upd.getMessage());
        return Reply.of(action, Flag.TEXT, upd -> responseHandler.userIsAdmin(getChatId(upd)), upd -> hasMessageWith(upd, START_PARSE_COMMAND));
    }

    private boolean hasMessageWith(Update update, String text) {
        return update.getMessage().hasText() && update.getMessage().getText().startsWith(text);
    }

}
