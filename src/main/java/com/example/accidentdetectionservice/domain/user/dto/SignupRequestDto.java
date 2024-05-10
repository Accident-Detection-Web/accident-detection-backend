package com.example.accidentdetectionservice.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequestDto {

    @Schema(description = "사용자 아이디", example = "username123")
    private String username;

    @Schema(description = "사용자 비밀번호", example = "password123")
    private String password;

    @Schema(description = "사용자 이메일", example = "email@email.com")
    private String email;
}
