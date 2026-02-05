package com.shapovalov.birthday.scheduler;

import com.shapovalov.birthday.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BirthdayScheduler {
    private final NotificationService notificationService;

    @Scheduled(cron = "${birthday.notification.cron}")
    public void scheduleBirthdayNotifications() {
        log.info("Schedule started");
        notificationService.sendBirthdayNotifications();
    }
}
