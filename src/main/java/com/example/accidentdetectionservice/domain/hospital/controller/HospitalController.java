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

    @GetMapping("/open-data")
    public ResponseEntity<List<HospitalResponseDto>> getHospitalData(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            return ResponseEntity.ok(hospitalService.getHospitalData(userDetails.getUser()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to get hospital open data");
        }
    }

    @GetMapping("/accident/combination")
    public ResponseEntity<AllDataResponseDto> getAllData(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            return ResponseEntity.ok(hospitalService.getAllData(userDetails.getUser()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to get hospital and accident all data");
        }
    }

    @GetMapping("/accident/statistics/month")
    public ResponseEntity<Map<String,Long>> getAccidentNumberOfMonth() {
        try {
            return ResponseEntity.ok(hospitalService.getAccidentNumberOfMonth());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get accident number of month");
        }
    }


    @GetMapping("/accident/statistics/region")
    public ResponseEntity<Map<String, Long>> getAccidentNumberOfRegion(){
        try {
            return ResponseEntity.ok(hospitalService.getAccidentNumberOfRegion());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get accident number of region");
        }
    }

}
