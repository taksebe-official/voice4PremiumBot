package ru.taksebe.telegram.premium.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramConfig {
    @Value("${telegram.webhook-path}")
    String webhookPath;
    @Value("${telegram.bot-name}")
    String botName;
    @Value("${telegram.bot-token}")
    String botToken;
    @Value("${message.too-big-voice.text}")
    String tooBigVoiceText;
    @Value("${message.illegal-message.text}")
    String illegalMessageText;
    @Value("${message.wtf.text}")
    String wtfText;
}