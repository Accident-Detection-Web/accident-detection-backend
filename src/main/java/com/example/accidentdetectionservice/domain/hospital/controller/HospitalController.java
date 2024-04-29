package com.example.accidentdetectionservice.domain.hospital.controller;

import com.example.accidentdetectionservice.domain.hospital.service.HospitalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/hospital")
public class HospitalController {

    private final HospitalService hospitalService;



}
