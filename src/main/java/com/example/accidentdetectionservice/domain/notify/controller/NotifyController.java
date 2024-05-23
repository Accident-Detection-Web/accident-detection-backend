package com.example.accidentdetectionservice.domain.notify.controller;

import com.example.accidentdetectionservice.domain.notify.dto.NotifyMessage;
import com.example.accidentdetectionservice.domain.notify.entity.Notify;
import com.example.accidentdetectionservice.domain.notify.service.NotifyService;
import com.example.accidentdetectionservice.domain.user.dto.MessageResponseDto;
import com.example.accidentdetectionservice.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notify")
public class NotifyController {

    private final NotifyService notifyService;

    /**
     * @apiNote 1.클라이언트가 로그인 시점에 해당 api 를 호출하여 SseEmitter 객체에 구독
     * @apiNote 2.Flask Server 로 부터 데이터를 받는 시점 Consumer Server 에서 알림 객체가 생성 및 알림 생성후 프론트에게 자동 전달
     * @param userDetails
     * @param lastEventId
     * @return
     */
    @GetMapping(value = "/subscribe", produces = MediaType.ALL_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(notifyService.subscribe(userDetails.getUser().getUsername(), lastEventId));
    }

//    @PostMapping("/send")
//    public ResponseEntity<MessageResponseDto> send(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        notifyService.send(userDetails.getUser(), Notify.NotificationType.ACCIDENT, NotifyMessage.ACCIDENT_DETECTION.getMessage(), null);
//        return ResponseEntity.ok(new MessageResponseDto("알림이 성공적으로 전송되었습니다.", HttpStatus.OK.value()));
//    }

}
