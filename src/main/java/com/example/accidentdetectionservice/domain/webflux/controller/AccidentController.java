package com.example.accidentdetectionservice.domain.webflux.controller;

import com.example.accidentdetectionservice.domain.webflux.dto.AccidentRequestDto;
import com.example.accidentdetectionservice.domain.webflux.dto.AccidentResponseDto;
import com.example.accidentdetectionservice.domain.webflux.service.AccidentService;
import com.example.accidentdetectionservice.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/webflux")
public class AccidentController {

    private final AccidentService accidentService;

    @PostMapping("/receive-data")
    public Mono<ResponseEntity<AccidentResponseDto>> receiveData(@RequestBody AccidentRequestDto requestDto,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails){
        accidentService.sendNotifyClient(userDetails.getUser());

        return accidentService.processAccidentData(requestDto, userDetails.getUser())
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}
