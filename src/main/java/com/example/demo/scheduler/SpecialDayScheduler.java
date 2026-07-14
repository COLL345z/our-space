package com.example.demo.scheduler;

import com.example.demo.entity.SpecialDay;
import com.example.demo.repository.SpecialDayRepository;
import com.example.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class SpecialDayScheduler {

    @Autowired
    private SpecialDayRepository specialDayRepository;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron = "0 0 9 * * *")
    public void checkUpcomingSpecialDays() {
        LocalDate today = LocalDate.now();
        
        List<SpecialDay> allDays = specialDayRepository.findAll();
        
        for (SpecialDay day : allDays) {
            LocalDate eventDate = day.getEventDate();
            long daysUntil = ChronoUnit.DAYS.between(today, eventDate);
            
            // Send reminder for 7, 3, 1 day before and day of
            if (daysUntil == 7 || daysUntil == 3 || daysUntil == 1 || daysUntil == 0) {
                String reminderMessage = getReminderMessage(daysUntil, day.getDescription());
                
                notificationService.notifyPartner(
                    day.getCreatedBy() != null ? day.getCreatedBy() : "Someone",
                    "📅 Special Day Reminder!",
                    reminderMessage
                );
            }
            
            // ✅ Check recurring events using the getter method
            if (day.isRecurring() && daysUntil < 0) {
                LocalDate nextOccurrence = getNextOccurrence(day.getEventDate(), today);
                if (nextOccurrence != null) {
                    long newDaysUntil = ChronoUnit.DAYS.between(today, nextOccurrence);
                    if (newDaysUntil == 7 || newDaysUntil == 3 || newDaysUntil == 1 || newDaysUntil == 0) {
                        String reminderMessage = getReminderMessage(newDaysUntil, day.getDescription() + " (recurring)");
                        
                        notificationService.notifyPartner(
                            day.getCreatedBy() != null ? day.getCreatedBy() : "Someone",
                            "📅 Recurring Special Day Reminder!",
                            reminderMessage
                        );
                    }
                }
            }
        }
    }

    private String getReminderMessage(long daysUntil, String description) {
        return switch ((int) daysUntil) {
            case 0 -> "🎉 Today is " + description + "! 🎉";
            case 1 -> "⏰ Tomorrow is " + description + "! Get ready!";
            case 3 -> "📌 " + description + " is in 3 days!";
            case 7 -> "🗓️ " + description + " is in 1 week!";
            default -> description + " is coming up soon!";
        };
    }

    private LocalDate getNextOccurrence(LocalDate eventDate, LocalDate today) {
        LocalDate next = eventDate.withYear(today.getYear());
        
        if (next.isBefore(today) || next.equals(today)) {
            next = next.plusYears(1);
        }
        
        return next;
    }
}
