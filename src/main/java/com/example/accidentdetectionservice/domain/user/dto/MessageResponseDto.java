package com.example.accidentdetectionservice.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageResponseDto {

    private String message;
    private int statusCode;
}
