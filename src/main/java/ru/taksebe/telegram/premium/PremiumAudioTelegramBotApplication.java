package ru.taksebe.telegram.premium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PremiumAudioTelegramBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(PremiumAudioTelegramBotApplication.class, args);
    }
}