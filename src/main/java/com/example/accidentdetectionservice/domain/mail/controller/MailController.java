package com.example.accidentdetectionservice.domain.mail.controller;

import com.example.accidentdetectionservice.domain.mail.service.MailService;
import com.example.accidentdetectionservice.domain.user.dto.MessageResponseDto;
import com.example.accidentdetectionservice.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;

    @PostMapping("/trasmission")
    public ResponseEntity<Void> sendMail(@AuthenticationPrincipal UserDetailsImpl userDetails){

        try{
            // api 호출시에 mail DB 에서 해당 User 의 entity 가져오기
            // mailEventDto 에 해당 정보 저장


            return ResponseEntity.ok().build();
        } catch (Exception e){
            throw new RuntimeException("Failed to send email");
        }

    }

}
