package com.example.accidentdetectionservice.domain.mail.entity;

import com.example.accidentdetectionservice.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor
public class MailEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mail_event_id")
    private Long id;

    private byte[] attachPng;

    @ManyToOne
    @JoinColumn(name = "memeber_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User receiver;



}
