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
	    if (event.getTargetUserId() != null) {
	        // 특정 유저에게만 전송
	        alarmService.sendToUser(event.getTargetUserId(), event.getMessage());
	    } else {
	        // 전체 전송
	        alarmService.sendToAll(event.getMessage());
	    }
	}
}
