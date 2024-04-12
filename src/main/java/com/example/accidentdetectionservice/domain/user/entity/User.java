package com.example.accidentdetectionservice.domain.user.entity;

import com.example.accidentdetectionservice.domain.notify.aop.proxy.NotifyInfo;
import com.example.accidentdetectionservice.domain.notify.entity.Notify;
import com.example.accidentdetectionservice.domain.notify.entity.Notify.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User implements NotifyInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;


    public User(String username, String password, String email, UserRoleEnum role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    @Override
    public User getReceiver() {
        return this;
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.ACCIDENT;
    }
}
