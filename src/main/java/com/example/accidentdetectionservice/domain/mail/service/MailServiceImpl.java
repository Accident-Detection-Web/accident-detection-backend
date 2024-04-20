package com.example.accidentdetectionservice.domain.mail.service;

import com.example.accidentdetectionservice.domain.mail.dto.MailEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService{
    @Override
    public void sendMail(MailEventDto mailEventDto) {

    }
}
