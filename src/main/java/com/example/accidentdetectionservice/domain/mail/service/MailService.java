package com.example.accidentdetectionservice.domain.mail.service;

import com.example.accidentdetectionservice.domain.mail.dto.MailEventDto;

public interface MailService {

    public void sendMail(MailEventDto mailEventDto);
}
