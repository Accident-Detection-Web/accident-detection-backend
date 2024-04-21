package com.example.accidentdetectionservice.domain.mail.repository;

import com.example.accidentdetectionservice.domain.mail.entity.MailEvent;
import com.example.accidentdetectionservice.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailRepository extends JpaRepository<MailEvent, Long> {

    MailEvent findByReceiver(User receiver);
}
