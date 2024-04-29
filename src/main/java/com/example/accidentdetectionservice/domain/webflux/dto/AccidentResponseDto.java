package com.example.accidentdetectionservice.domain.webflux.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccidentResponseDto {

    private Boolean accident;
    private String pngData; // png 파일의 데이터를 저장할 필드
    private String latitude; // 위도
    private String longitude; // 경도
    private String address;
    private String time;
    private Long severityLevel;
    private String severity;

    public AccidentResponseDto(AccidentRequestDto requestDto) {
        this.accident = requestDto.getAccident();
        this.pngData = requestDto.getPngData();
        this.latitude = requestDto.getLatitude();
        this.longitude = requestDto.getLongitude();
//        this.address = // 위도 경도 변환 값;
        this.time = requestDto.getTime();
        this.severityLevel = requestDto.getSeverityLevel();
        this.severity = requestDto.getSeverity();
    }

}
