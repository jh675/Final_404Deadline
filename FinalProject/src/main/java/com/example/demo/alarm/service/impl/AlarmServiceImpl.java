package com.example.demo.alarm.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.alarm.service.AlarmService;

@Service
public class AlarmServiceImpl implements AlarmService {

    private static final long SSE_TIMEOUT_MS = 30L * 60 * 1000;

    private final Map<String, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);

        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(emitter);

        Runnable cleanup = () -> removeEmitter(userId, emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());

        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            cleanup.run();
        }

        return emitter;
    }

    private void removeEmitter(String userId, SseEmitter emitter) {
        Set<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters == null) {
            return;
        }
        userEmitters.remove(emitter);
        if (userEmitters.isEmpty()) {
            emitters.remove(userId, userEmitters);
        }
    }

    private void completeEmitter(String userId, SseEmitter emitter) {
        removeEmitter(userId, emitter);
        try {
            emitter.complete();
        } catch (Exception ignored) {
        }
    }

    @Scheduled(fixedRate = 30000)
    public void sendHeartbeat() {
        emitters.forEach((userId, userEmitters) -> {
            for (SseEmitter emitter : userEmitters) {
                try {
                    emitter.send(SseEmitter.event().comment("heartbeat"));
                } catch (IOException e) {
                    completeEmitter(userId, emitter);
                }
            }
        });
    }

    public void sendToAll(String message) {
        emitters.forEach((userId, userEmitters) -> {
            for (SseEmitter emitter : userEmitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(message));
                } catch (IOException e) {
                    completeEmitter(userId, emitter);
                }
            }
        });
    }

    public void sendToUser(String userId, String message) {
        Set<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters == null || userEmitters.isEmpty()) {
            return;
        }
        for (SseEmitter emitter : userEmitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(message));
            } catch (IOException e) {
                completeEmitter(userId, emitter);
            }
        }
    }
}
