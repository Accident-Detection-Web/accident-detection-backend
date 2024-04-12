package com.example.accidentdetectionservice.domain.user.controller;

import com.example.accidentdetectionservice.domain.user.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MailController {

    private final MailService mailService;

    @ResponseBody
    @PostMapping("/sign-up/mail")
    public ResponseEntity<String> mailSend(String mail) {
        int number = mailService.sendMail(mail);

        return ResponseEntity.ok().body("" + number);
    }

}
