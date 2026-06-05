package com.example.demo.alarm.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class NotificationEvent extends ApplicationEvent {
	private final String message;
    private final String targetUserId;  // ← 특정 유저 (null이면 전체)

    // 전체 전송용
    public NotificationEvent(Object source, String message) {
        super(source);
        this.message = message;
        this.targetUserId = null;
    }

    // 특정 유저 전송용
    public NotificationEvent(Object source, String message, String targetUserId) {
        super(source);
        this.message = message;
        this.targetUserId = targetUserId;
    }
}
