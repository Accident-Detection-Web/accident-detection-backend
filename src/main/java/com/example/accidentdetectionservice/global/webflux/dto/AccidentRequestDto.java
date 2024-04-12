package com.example.accidentdetectionservice.global.webflux.dto;

import java.util.Arrays;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccidentRequestDto {

    // webflux 에서 데이터를 주고받을 대는 주로 json 형식 사용
    private Boolean accident;
    private String pngData; // png 파일의 데이터를 저장할 필드
    private String latitude; // 위도
    private String longitude; // 경도

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
