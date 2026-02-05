package com.shapovalov.birthday.service;

import com.shapovalov.birthday.dto.PersonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService extends TelegramLongPollingBot {
    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.chat-ids}")
    private List<String> chatIds;

    private final PersonService personService;

    @Override
    public String getBotUsername() {
        return "BirthdayReminderBot";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Не обрабатываем входящие сообщения
    }
    public void sendBirthdayNotifications() {
        List<PersonResponseDto> todayBirthdays = personService.getAllPersons().stream()
                .filter(p -> p.getDaysUntilBirthday() == 0)
                .toList();

        List<PersonResponseDto> tomorrowBirthdays = personService.getAllPersons().stream()
                .filter(p -> p.getDaysUntilBirthday() == 1)
                .toList();

        if (todayBirthdays.isEmpty() && tomorrowBirthdays.isEmpty()) {
            log.info("No upcoming birthdays");
            return;
        }

        String message = buildMessage(todayBirthdays, tomorrowBirthdays);

        for (String chatId : chatIds) {
            sendMessage(chatId, message);
        }
    }
    private String buildMessage(List<PersonResponseDto> today, List<PersonResponseDto> tomorrow) {
        StringBuilder message = new StringBuilder();

        if (!today.isEmpty()) {
            message.append("Сегодня день рождения:\n");
            for (PersonResponseDto person : today) {
                message.append(person.getName()).append("\n");
            }
        }

        if (!tomorrow.isEmpty()) {
            if (!today.isEmpty()) {
                message.append("\n");
            }
            message.append("Завтра день рождения:\n");
            for (PersonResponseDto person : tomorrow) {
                message.append(person.getName()).append("\n");
            }
        }

        return message.toString();
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
            log.info("Message sent at: {}", chatId);
        } catch (TelegramApiException e) {
            log.error("Error on sending message: {}", e.getMessage());
        }
    }
}
