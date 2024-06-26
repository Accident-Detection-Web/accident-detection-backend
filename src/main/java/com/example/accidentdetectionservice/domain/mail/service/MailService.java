package com.example.accidentdetectionservice.domain.mail.service;

import com.example.accidentdetectionservice.domain.user.dto.MessageResponseDto;
import com.example.accidentdetectionservice.domain.user.entity.User;

public interface MailService {

    public void sendMail(User receiver);

    public MessageResponseDto createMailEventEntity(User user);
}
