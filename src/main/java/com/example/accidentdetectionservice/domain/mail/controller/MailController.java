package com.example.accidentdetectionservice.domain.mail.controller;

import com.example.accidentdetectionservice.domain.mail.service.MailService;
import com.example.accidentdetectionservice.domain.user.dto.MessageResponseDto;
import com.example.accidentdetectionservice.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;


    @GetMapping("/produce")
    public ResponseEntity<MessageResponseDto> createMailEventEntity(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        try{

            return ResponseEntity.ok(mailService.createMailEventEntity(userDetails.getUser()));
        } catch (Exception exception){
            throw new RuntimeException("Failed to create mail_event Entity", exception);
        }
    }

    @PostMapping("/transmission")
    public ResponseEntity<Void> sendMail(@AuthenticationPrincipal UserDetailsImpl userDetails){

        try{
            mailService.sendMail(userDetails.getUser());

            return ResponseEntity.ok().build();
        } catch (Exception exception){
            throw new RuntimeException("Failed to send email", exception);
        }

    }

}
