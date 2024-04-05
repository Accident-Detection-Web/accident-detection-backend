package com.example.accidentdetectionservice.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class SignupRequestDto {

    private String username;
    private String password;
    private String email;
}
