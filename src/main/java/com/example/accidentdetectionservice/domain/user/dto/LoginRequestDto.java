package com.example.accidentdetectionservice.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class LoginRequestDto {

    private String username;
    private String password;

}
