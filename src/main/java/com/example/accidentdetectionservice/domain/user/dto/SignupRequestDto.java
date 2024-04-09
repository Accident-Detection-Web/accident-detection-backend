package com.example.accidentdetectionservice.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequestDto {

    private String username;
    private String password;
    private String email;
}
