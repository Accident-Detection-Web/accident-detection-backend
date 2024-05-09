package com.example.accidentdetectionservice.domain.accident.controller;

import com.example.accidentdetectionservice.domain.accident.dto.AccidentRequestDto;
import com.example.accidentdetectionservice.domain.accident.service.AccidentService;
import com.example.accidentdetectionservice.domain.user.dto.MessageResponseDto;
import com.example.accidentdetectionservice.domain.user.entity.User;
import com.example.accidentdetectionservice.global.security.UserDetailsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/accident")
public class AccidentController {

    private final AccidentService accidentService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/receiving-data", consumes = "multipart/form-data")
    public ResponseEntity<MessageResponseDto> processFileAndData(@RequestParam("image") MultipartFile image,
                                                   @RequestParam("requestDto") String requestDtoJson,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {

            AccidentRequestDto requestDto = objectMapper.readValue(requestDtoJson, AccidentRequestDto.class);
            byte[] imageBytes = image.getBytes();

            return ResponseEntity.ok(accidentService.processFileAndData(imageBytes, requestDto, userDetails.getUser()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Error Processing the image");
        }
    }

}
