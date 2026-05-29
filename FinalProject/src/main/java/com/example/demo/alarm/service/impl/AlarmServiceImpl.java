package com.example.demo.alarm.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.alarm.service.AlarmService;

@Service
public class AlarmServiceImpl implements AlarmService {

    private static final long HEARTBEAT_INTERVAL_MS = 30_000;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 사용자 SSE 연결
    @Override
    public SseEmitter subscribe(String userId) {
        // 같은 userId로 재구독 시 이전 emitter complete 후 교체
        SseEmitter existing = emitters.get(userId);
        if (existing != null) {
            existing.complete();
        }

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(userId, emitter);

        // 연결 종료 시 해당 인스턴스만 제거
        emitter.onCompletion(() -> emitters.remove(userId, emitter));
        emitter.onTimeout(() -> emitters.remove(userId, emitter));
        emitter.onError(e -> emitters.remove(userId, emitter));

        // 연결 직후 heartbeat 1회 즉시 전송
        sendHeartbeat(userId, emitter);

        return emitter;
    }

    // 전체 사용자에게 알림 전송
    @Override
    public void sendToAll(String message) {
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(message));
            } catch (IOException e) {
                emitters.remove(userId, emitter);
            }
        });
    }

    // 특정 사용자에게만 알림 전송
    @Override
    public void sendToUser(String userId, String message) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(message));
            } catch (IOException e) {
                emitters.remove(userId, emitter);
            }
        }
    }

    // 30초마다 heartbeat 전송
    @Scheduled(fixedRate = HEARTBEAT_INTERVAL_MS)
    public void broadcastHeartbeat() {
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                    .name("heartbeat")
                    .data("ping"));
            } catch (IOException e) {
                emitters.remove(userId, emitter);
            }
        });
    }

    // 단일 사용자 heartbeat 전송
    private void sendHeartbeat(String userId, SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event()
                .name("heartbeat")
                .data("ping"));
        } catch (IOException e) {
            emitters.remove(userId, emitter);
        }
    }
}