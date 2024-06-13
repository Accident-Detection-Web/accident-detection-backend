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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EmitterRepository emitterRepository;
    private final NotifyRepository notifyRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 상태 추적을 위한 맵
    private final ConcurrentMap<String, Boolean> emitterCompletionStatus = new ConcurrentHashMap<>();

    @Transactional
    public SseEmitter subscribe(String username, String lastEventId) {
        String name = "sse";
        String emitterId;
        SseEmitter emitter;

        // 이미 존재하는 emitter 확인
        Map<String, SseEmitter> existingEmitters = emitterRepository.findAllEmitterStartWithByUserId(username);
        if (!existingEmitters.isEmpty()) {
            log.info("Emitter already exists for username: {}", username);
            Map.Entry<String, SseEmitter> entry = existingEmitters.entrySet().iterator().next();
            emitterId = entry.getKey();
            emitter = entry.getValue();
        } else {
            // 존재하지 않으면 새로운 emitter 생성 및 저장
            emitterId = makeTimeIncludeId(username);
            emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
            emitterCompletionStatus.put(emitterId, false); // 초기 상태 설정
        }

        emitter.onCompletion(() -> {
            log.info("Emitter completed for emitterId: {}", emitterId);
            emitterCompletionStatus.put(emitterId, true); // 완료 상태로 설정

//            emitterRepository.deleteById(emitterId);
        });
        emitter.onTimeout(() -> {
            log.info("Emitter timed out for emitterId: {}", emitterId);
            emitterCompletionStatus.put(emitterId, true); // 완료 상태로 설정
            emitterRepository.deleteById(emitterId);
        });
        // 503 에러 방지를 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(username);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userEmail=" + username + "]", name);

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, username, emitterId, emitter);
        }
        // 주기적인 더미 이벤트 전송
        sendPeriodicDummyEvent(emitterId, username);
//        sendPeriodicDummyEvent(emitter, username);

        return emitter;
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, String userEmail, String emitterId,
        SseEmitter emitter) {
        String name = "lostData";
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userEmail));
        eventCaches.entrySet().stream()
            .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
            .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue(), name));
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data, String name) {
        try {
                String jsonData = objectMapper.writeValueAsString(data);
                emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name(name)
                    .data(jsonData)
            );
        } catch (IOException exception) {
//            log.error("Error sendNotification = {}", exception.toString());
            emitterCompletionStatus.put(emitterId, true); // 예외 발생 시 완료 상태로 설정
//            emitterRepository.deleteById(emitterId);
        } catch (IllegalStateException exception) {
//            log.error("Emitter already completed: {}", emitterId);
            emitterCompletionStatus.put(emitterId, true); // 예외 발생 시 완료 상태로 설정
//            emitterRepository.deleteById(emitterId);
        }
    }

    private String makeTimeIncludeId(String email) {
        return email + "_" + System.currentTimeMillis();
    }

    @Transactional
    public void send(User receiver, Notify.NotificationType notificationType, String content,
        String url) {
        String name = "accident";
        Notify notification = notifyRepository.save(createNotification(receiver, notificationType, content, url));
        String receiverEmail = receiver.getUsername();
        String eventId = receiverEmail + "_" + System.currentTimeMillis();
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(receiverEmail);

        log.info("username = {}", receiverEmail);
        log.info("emitters = {}", emitters);

        emitters.entrySet().stream().findFirst().ifPresent(entry -> {
            log.info("First Emitter Key = {}", entry.getKey());
            log.info("First Emitter Value = {}", entry.getValue());
        });

        emitters.forEach((key, emitter) -> {
                emitterRepository.saveEventCache(key, notification);
                sendNotification(emitter, eventId, key, createResponse(notification), name);

        });
//        emitters.forEach(
//            (key, emitter) ->{
//                emitterRepository.saveEventCache(key, notification);
//                sendNotification(emitter, eventId, key, createResponse(notification), name);
//            }
//        );

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

    private void sendPeriodicDummyEvent(String emitterId, String username) { // undo : SseEmitter emitter, String username
        String name = "sse";
        Runnable runnable = () -> {
            while (true) {
                try {
                    Thread.sleep(1000 * 30); // 30초마다 더미 이벤트 전송
                    SseEmitter emitter = emitterRepository.findById(emitterId);
                    sendNotification(emitter, makeTimeIncludeId(username), emitterId, "Dummy event", name);

//                    sendNotification(emitter, makeTimeIncludeId(username), makeTimeIncludeId(username), "Dummy event", name);
                }  catch (InterruptedException e) {
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
        String name = "sse";
        scheduler.scheduleAtFixedRate(() -> {
            try {
                String heartbeatEventId = makeTimeIncludeId(username);
                sendNotification(emitter, heartbeatEventId, emitterId, "Heartbeat",name);
            } catch (Exception e) {
                // 예외 처리 필요 시 추가
                emitter.completeWithError(e);
                scheduler.shutdown();
            }
        }, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }
}
