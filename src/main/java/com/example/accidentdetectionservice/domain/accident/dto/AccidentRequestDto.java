package com.example.accidentdetectionservice.domain.accident.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccidentRequestDto {

    private Boolean accident;
    private String latitude; // 위도
    private String longitude; // 경도
    private String date;
    private Long severityLevel;
    private String severity;

}
