package com.example.accidentdetectionservice.domain.accident.controller;

import com.example.accidentdetectionservice.domain.accident.dto.AccidentRequestDto;
import com.example.accidentdetectionservice.domain.accident.service.AccidentService;
import com.example.accidentdetectionservice.domain.user.dto.MessageResponseDto;
import com.example.accidentdetectionservice.domain.user.entity.User;
import com.example.accidentdetectionservice.global.security.UserDetailsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accident")
@Slf4j(topic = "Flask 서버로부터 사고 데이터 수신")
public class AccidentController {

    private final AccidentService accidentService;
    private final ObjectMapper objectMapper;

    /**
     * @apiNote 해당 api 실행이 되면 프론트 측에서는
     * 1. HospitalController 의 getHospitalData API 실행
     * 2. MailController 의 createMailEventEntity API 실행
     * 3. MailController 의 sendMail API 실행
     * @param image
     * @param requestDtoJson
     * @param userDetails
     * @return
     */
    @PostMapping(value = "/receiving-data", consumes = "multipart/form-data")
    public ResponseEntity<MessageResponseDto> processFileAndData(@RequestParam("image") MultipartFile image,
                                                   @RequestParam("requestDto") String requestDtoJson,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {

            AccidentRequestDto requestDto = objectMapper.readValue(requestDtoJson, AccidentRequestDto.class);
            byte[] imageBytes = image.getBytes();

            return ResponseEntity.ok(accidentService.processFileAndData(imageBytes, requestDto, userDetails.getUser()));
        } catch (Exception exception) {
            // GlobalExceptionHandler
            throw new IllegalArgumentException(exception);
        }
    }

}
