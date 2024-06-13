package com.example.accidentdetectionservice.domain.hospital.dto;

import com.example.accidentdetectionservice.domain.accident.entity.Accident;
import java.util.Base64;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DetailsResponseDto {

    private String longitude; // 위도
    private String latitude; // 경도
    private String sorting;
    private String accuracy;
    private String date;
    private String attachPngBase64; // Base64로 인코딩된 PNG 데이터
//    private byte[] attachPng;

    public DetailsResponseDto(Accident accident) {
        this.longitude = accident.getLongitude();
        this.latitude = accident.getLatitude();
        this.sorting = accident.getSorting();
        this.accuracy = accident.getAccuracy();
        this.date = accident.getDate();
        this.attachPngBase64 = Base64.getEncoder().encodeToString(accident.getAttachPng());
    }
}
