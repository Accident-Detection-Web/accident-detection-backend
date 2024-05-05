package com.example.accidentdetectionservice.domain.notify.controller;

import com.example.accidentdetectionservice.domain.notify.dto.NotifyMessage;
import com.example.accidentdetectionservice.domain.notify.entity.Notify;
import com.example.accidentdetectionservice.domain.notify.service.NotifyService;
import com.example.accidentdetectionservice.domain.user.dto.MessageResponseDto;
import com.example.accidentdetectionservice.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notify")
public class NotifyController {

    private final NotifyService notifyService;

    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId){
        return ResponseEntity.ok(notifyService.subscribe(userDetails.getUser().getUsername(), lastEventId));
    }

//    @PostMapping("/send")
//    public ResponseEntity<MessageResponseDto> send(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        notifyService.send(userDetails.getUser(), Notify.NotificationType.ACCIDENT, NotifyMessage.ACCIDENT_DETECTION.getMessage(), null);
//        return ResponseEntity.ok(new MessageResponseDto("알림이 성공적으로 전송되었습니다.", HttpStatus.OK.value()));
//    }

}
