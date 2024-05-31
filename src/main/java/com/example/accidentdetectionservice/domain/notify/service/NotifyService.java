package com.example.accidentdetectionservice.domain.notify.service;

import static com.example.accidentdetectionservice.domain.notify.dto.NotifyDto.*;
import static com.example.accidentdetectionservice.domain.notify.dto.NotifyDto.Response.*;
import static com.example.accidentdetectionservice.domain.notify.entity.Notify.*;

import com.example.accidentdetectionservice.domain.notify.dto.NotifyDto;
import com.example.accidentdetectionservice.domain.notify.entity.Notify;
import com.example.accidentdetectionservice.domain.notify.entity.Notify.NotificationType;
import com.example.accidentdetectionservice.domain.notify.repository.EmitterRepository;
import com.example.accidentdetectionservice.domain.notify.repository.NotifyRepository;
import com.example.accidentdetectionservice.domain.user.entity.User;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotifyService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60 * 24;
    private static final Long HEARTBEAT_INTERVAL = 30L * 1000; // 30 seconds

    private final EmitterRepository emitterRepository;
    private final NotifyRepository notifyRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Transactional
    public SseEmitter subscribe(String username, String lastEventId) {
        String emitterId = makeTimeIncludeId(username);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 503 에러 방지를 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(username);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userEmail=" + username + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, username, emitterId, emitter);
        }
        // 주기적인 더미 이벤트 전송
//        sendPeriodicDummyEvent(emitter, username);

        return emitter;
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, String userEmail, String emitterId,
        SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userEmail));
        eventCaches.entrySet().stream()
            .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
            .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
                emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("sse")
                    .data(data)
            );
        } catch (IOException exception) {
            log.error("Error sendNotification = {}", exception.toString());
            emitterRepository.deleteById(emitterId);
        }
    }

    private String makeTimeIncludeId(String email) {
        return email + "_" + System.currentTimeMillis();
    }

    @Transactional
    public void send(User receiver, NotificationType notificationType, String content,
        String url) {
        Notify notification = notifyRepository.save(createNotification(receiver, notificationType, content, url));

        String receiverEmail = receiver.getEmail();
        String eventId = receiverEmail + "_" + System.currentTimeMillis();
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(receiverEmail);
        emitters.forEach(
            (key, emitter) ->{
                emitterRepository.saveEventCache(key, notification);
                sendNotification(emitter, eventId, key, createResponse(notification));
            }
        );

    }

    private Notify createNotification(User receiver, NotificationType notificationType,
        String content, String url) {
        return Notify.builder()
            .receiver(receiver)
            .notificationType(notificationType)
            .content(content)
            .url(url)
            .isRead(false)
            .build();
    }

    private void sendPeriodicDummyEvent(SseEmitter emitter, String username) {
        Runnable runnable = () -> {
            while (true) {
                try {
                    Thread.sleep(30000); // 30초마다 더미 이벤트 전송
                    sendNotification(emitter, makeTimeIncludeId(username), makeTimeIncludeId(username), "Dummy event");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error(e.toString());
                    break;
                }
            }
        };
        new Thread(runnable).start();
    }

    private void startHeartbeat(SseEmitter emitter, String username, String emitterId) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                String heartbeatEventId = makeTimeIncludeId(username);
                sendNotification(emitter, heartbeatEventId, emitterId, "Heartbeat");
            } catch (Exception e) {
                // 예외 처리 필요 시 추가
                emitter.completeWithError(e);
                scheduler.shutdown();
            }
        }, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }
}
