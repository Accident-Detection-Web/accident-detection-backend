package com.example.accidentdetectionservice.domain.mail.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MailEventDto {

    private String toAddress;
    private String subject;
    private String content;
    private byte[] attachPng;

}
