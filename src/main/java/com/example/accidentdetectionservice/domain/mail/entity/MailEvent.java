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

    @Column(name = "to_address")
    private String toAddress;

    @Column(name = "subject")
    private String subject;

    @Column(name = "content")
    private String content;

    @Lob
    @Column(name = "attach_png")
    private byte[] attachPng;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User receiver;


    public MailEvent(String toAddress, String subject, String content, byte[] attachPng, User receiver) {
        this.toAddress = toAddress;
        this.subject = subject;
        this.content = content;
        this.attachPng = attachPng;
        this.receiver = receiver;
    }
}
