package com.example.accidentdetectionservice.domain.user.controller;

import com.example.accidentdetectionservice.domain.user.service.SignupMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/users")
public class SignupMailController {

    private final SignupMailService signupMailService;

    @ResponseBody
    @PostMapping("/sign-up/mail")
    public ResponseEntity<String> mailSend(String mail) {
        int number = signupMailService.sendMail(mail);

        return ResponseEntity.ok().body("" + number);
    }

}
