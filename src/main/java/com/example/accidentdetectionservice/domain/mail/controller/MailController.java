package com.example.accidentdetectionservice.domain.mail.controller;

import com.example.accidentdetectionservice.domain.mail.service.MailService;
import com.example.accidentdetectionservice.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;


    @PostMapping("/transmission")
    public ResponseEntity<Void> sendMail(@AuthenticationPrincipal UserDetailsImpl userDetails){

        try{
            mailService.sendMail(userDetails.getUser());

            return ResponseEntity.ok().build();
        } catch (Exception e){
            throw new RuntimeException("Failed to send email");
        }

    }
}
