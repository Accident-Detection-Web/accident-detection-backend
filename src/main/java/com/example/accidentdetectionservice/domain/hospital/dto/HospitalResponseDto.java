package com.example.accidentdetectionservice.domain.hospital.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class HospitalResponseDto {

    private String name;
    private String tel;

    public HospitalResponseDto(String name, String tel) {
        this.name = name;
        this.tel = tel;
    }
}
