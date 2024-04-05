package com.example.accidentdetectionservice.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRoleEnum {

    USER("ROLE_USER");

    private final String authority;
}
