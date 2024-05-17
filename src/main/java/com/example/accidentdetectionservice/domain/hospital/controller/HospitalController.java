package com.example.accidentdetectionservice.domain.hospital.controller;

import com.example.accidentdetectionservice.domain.hospital.dto.AllDataResponseDto;
import com.example.accidentdetectionservice.domain.hospital.service.HospitalService;
import com.example.accidentdetectionservice.domain.hospital.dto.HospitalResponseDto;
import com.example.accidentdetectionservice.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hospital")
public class HospitalController {

    private final HospitalService hospitalService;

    /**
     * @apiNote 가용 가능한 실시간 병원 데이터 조회 메서드
     * @param userDetails
     * @return
     */
    @GetMapping("/open-data")
    public ResponseEntity<List<HospitalResponseDto>> getHospitalData(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            return ResponseEntity.ok(hospitalService.getHospitalData(userDetails.getUser()));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * @apiNote 해당 유저의 모든 사고 정보
     * @param userDetails
     * @return
     */
    @GetMapping("/accident/combination")
    public ResponseEntity<List<AllDataResponseDto>> getAllData(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            return ResponseEntity.ok(hospitalService.getAllData(userDetails.getUser()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to get hospital and accident all data");
        }
    }

    /**
     * @apiNote 모든 유저의 년도 및 월별 사고 횟수
     * @return
     */
    @GetMapping("/accident/statistics/month")
    public ResponseEntity<Map<String,Long>> getAccidentNumberOfMonth() {
        try {
            return ResponseEntity.ok(hospitalService.getAccidentNumberOfMonth());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get accident number of month");
        }
    }

    /**
     * @apiNote 모든 유저의 지역별 사고 횟수 (지역별 인구 순위로 저장)
     * @return
     */
    @GetMapping("/accident/statistics/region")
    public ResponseEntity<Map<String, Long>> getAccidentNumberOfRegion(){
        try {
            return ResponseEntity.ok(hospitalService.getAccidentNumberOfRegion());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get accident number of region");
        }
    }

}
