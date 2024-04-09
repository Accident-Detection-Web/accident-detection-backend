package com.example.accidentdetectionservice.global.redis.entity;

import com.example.accidentdetectionservice.domain.user.entity.UserRoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshToken {

    private String username;
    private UserRoleEnum role;

    private Long key;

    public RefreshToken(String username, UserRoleEnum role, Long key) {
        this.username = username;
        this.role = role;
        this.key = key;
    }
}
