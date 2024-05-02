package com.example.accidentdetectionservice.domain.user.controller;

import com.example.accidentdetectionservice.domain.user.dto.SignupMailRequestDto;
import com.example.accidentdetectionservice.domain.user.service.SignupMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/users")
public class SignupMailController {

    private final SignupMailService signupMailService;

    @PostMapping("/sign-up/mail")
    public ResponseEntity<String> mailSend(@RequestBody SignupMailRequestDto mail) {
        int number = signupMailService.sendMail(mail.getMail());

        return ResponseEntity.ok().body("" + number);
    }

}
