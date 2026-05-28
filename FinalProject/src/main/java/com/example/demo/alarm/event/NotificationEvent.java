package com.example.demo.alarm.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class NotificationEvent extends ApplicationEvent {
	 private final String message;

	    public NotificationEvent(Object source, String message) {
	        super(source);
	        this.message = message;
	    }
}
