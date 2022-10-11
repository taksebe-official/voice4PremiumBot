package ru.taksebe.telegram.premium.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.taksebe.telegram.premium.telegram.WriteReadBot;

@RestController
@AllArgsConstructor
public class WebhookController {
    private final WriteReadBot writeReadBot;

    @PostMapping("/premium")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return writeReadBot.onWebhookUpdateReceived(update);
    }
}