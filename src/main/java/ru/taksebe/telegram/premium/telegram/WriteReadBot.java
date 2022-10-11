package ru.taksebe.telegram.premium.telegram;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;
import ru.taksebe.telegram.premium.exceptions.TooBigVoiceMessageException;

import java.io.IOException;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WriteReadBot extends SpringWebhookBot {
    String botPath;
    String botUsername;
    String botToken;

    String tooBigVoiceText;
    String illegalMessageText;
    String wtfText;

    MessageHandler messageHandler;

    public WriteReadBot(SetWebhook setWebhook, MessageHandler messageHandler) {
        super(setWebhook);
        this.messageHandler = messageHandler;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            return handleUpdate(update);
        } catch (TooBigVoiceMessageException e) {
            return new SendMessage(update.getMessage().getChatId().toString(), this.tooBigVoiceText);
        } catch (IllegalArgumentException e) {
            return new SendMessage(update.getMessage().getChatId().toString(), this.illegalMessageText);
        } catch (Exception e) {
            return new SendMessage(update.getMessage().getChatId().toString(), this.wtfText);
        }
    }

    private BotApiMethod<?> handleUpdate(Update update) throws IOException {
        if (update.hasCallbackQuery()) {
            return null;
        } else {
            Message message = update.getMessage();
            if (message != null) {
                return messageHandler.answerMessage(message);
            }
            return null;
        }
    }
}