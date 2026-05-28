package com.example.demo.alarm.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AlarmService {
	SseEmitter subscribe(String userId);
	void sendToAll(String message);
	void sendToUser(String userId, String message);
}
