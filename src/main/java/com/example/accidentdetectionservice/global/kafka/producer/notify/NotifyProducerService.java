package com.example.accidentdetectionservice.global.kafka.producer.notify;

import com.example.accidentdetectionservice.domain.notify.dto.NotifyMessage;
import com.example.accidentdetectionservice.domain.notify.entity.Notify;
import com.example.accidentdetectionservice.domain.notify.service.NotifyService;
import com.example.accidentdetectionservice.domain.user.dto.MessageResponseDto;
import com.example.accidentdetectionservice.domain.user.entity.User;
import com.example.accidentdetectionservice.global.security.UserDetailsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotifyProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    public void sendAccidentDetectionNotification(User receiver) {

        try {
            String receiverMessage = objectMapper.writeValueAsString(receiver); // <- 흠
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("notify-1",
                    receiverMessage);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug(
                            "Send message=[ userId: " + receiver.getId()
                                    + " ] with partition=[" + result.getRecordMetadata().partition()
                                    + "], offset=[" + result.getRecordMetadata().offset() + "]");
                } else {
                    log.error("Unable to send message=[ userId: " + receiver.getId()
                            + " ] due to : " + ex.getMessage());
                }
            });
        } catch (JsonProcessingException e) {
            log.error("메세지 변환 실패");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
