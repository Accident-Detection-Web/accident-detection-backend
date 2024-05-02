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

import java.util.List;

@Controller
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
}
