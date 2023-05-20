package pro.sky.telegrambot.timer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
public class NotificationTaskTimer {

    private final NotificationTaskService notificationTaskService;
    private final TelegramBot telegramBot;

    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskTimer(NotificationTaskService notificationTaskService, TelegramBot telegramBot, NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskService = notificationTaskService;
        this.telegramBot = telegramBot;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void task() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        notificationTaskService
                .getAllNotificationsByDateTime(now)
                .forEach(notificationTask -> {
                    telegramBot.execute(new SendMessage(notificationTask.getChatId(), "Напоминание: " + notificationTask.getMessage()));
                    notificationTaskRepository.delete(notificationTask);
                });
    }
}