package ru.taksebe.telegram.premium.telegram;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Voice;
import ru.taksebe.telegram.premium.exceptions.TooBigVoiceMessageException;
import ru.taksebe.telegram.premium.utils.Converter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MessageHandler {
    Converter converter;
    TelegramApiClient telegramApiClient;
    String tempFileNamePrefix;

    public MessageHandler(Converter converter,
                          TelegramApiClient telegramApiClient,
                          @Value("${files.outgoing}") String tempFileNamePrefix) {
        this.converter = converter;
        this.telegramApiClient = telegramApiClient;
        this.tempFileNamePrefix = tempFileNamePrefix;
    }

    public BotApiMethod<?> answerMessage(Message message) throws IOException {
        if (message.hasVoice()) {
            convertVoice(message);
        } else if (message.getText() != null && message.getText().equals("/start")) {
            telegramApiClient.uploadStartPhoto(message.getChatId().toString());
        } else {
            throw new IllegalArgumentException();
        }
        return null;
    }

    private void convertVoice(Message message) throws IOException {
        Voice voice = message.getVoice();

        if (voice.getDuration() > 600) {
            throw new TooBigVoiceMessageException();
        }

        File source = telegramApiClient.getVoiceFile(voice.getFileId());
        File target = File.createTempFile(this.tempFileNamePrefix, ".mp3");

        try {
            converter.convertOggToMp3(source.getAbsolutePath(), target.getAbsolutePath());
        } catch (Exception e) {
            throw new IOException();
        }

        telegramApiClient.uploadAudio(message.getChatId().toString(),
                new ByteArrayResource(Files.readAllBytes(target.toPath())) {
                    @Override
                    public String getFilename() {
                        return "IlııIIIıııIııııııIIIIllıııııIıııııı.mp3";
                    }
                }
        );
    }
}