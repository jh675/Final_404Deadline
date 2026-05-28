package com.example.demo.alarm.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.demo.alarm.service.AlarmService;

@Component
public class NotificationEventListener {
		
	@Autowired
    private AlarmService alarmService;

    @Async
    @EventListener
    public void handleNotification(NotificationEvent event) {
        alarmService.sendToAll(event.getMessage());
    }
}
