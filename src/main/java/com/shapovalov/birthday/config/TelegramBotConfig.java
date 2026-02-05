package com.shapovalov.birthday.config;

import com.shapovalov.birthday.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Slf4j
public class TelegramBotConfig {
    @Bean
    public TelegramBotsApi telegramBotsApi(NotificationService notificationService) {
        TelegramBotsApi botsApi = null;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(notificationService);
            log.info("Telegram бот успешно зарегистрирован");
        } catch (TelegramApiException e) {
            log.error("Ошибка регистрации бота: {}", e.getMessage());
        }
        return botsApi;
    }
}
