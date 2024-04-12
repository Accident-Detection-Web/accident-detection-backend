package com.example.accidentdetectionservice.global.webflux.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccidentResponseDto {

    private Boolean accident;
    private String pngData; // png 파일의 데이터를 저장할 필드
    private String latitude; // 위도
    private String longitude; // 경도

    public AccidentResponseDto(AccidentRequestDto requestDto) {
        this.accident = requestDto.getAccident();
        this.pngData = requestDto.getPngData();
        this.latitude = requestDto.getLatitude();
        this.longitude = requestDto.getLongitude();
    }

    @Override
    public String toString() {
        return "AccidentRequestDto{" +
            "accident=" + accident +
            ", frame=" + pngData +
            ", latitude='" + latitude + '\'' +
            ", longitude='" + longitude + '\'' +
            '}';
    }
}