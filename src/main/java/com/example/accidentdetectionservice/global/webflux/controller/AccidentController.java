package com.example.accidentdetectionservice.global.webflux.controller;

import com.example.accidentdetectionservice.global.webflux.dto.AccidentRequestDto;
import com.example.accidentdetectionservice.global.webflux.dto.AccidentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class AccidentController {

    @PostMapping("/webflux/receive-data")
    public Mono<ServerResponse> receiveData(@RequestBody AccidentRequestDto requestDto){

        // 사용자에게 사고 감지 알림 보낸다. -> 로직 따로 구현, 상호작용은 프론트앤드쪽에서 담당
        // 위도와 경도를 주소로 변환한다.
        // 변환된 주소를 이용하여 해당 시/구 필터링을 걸처 수용가능한 병원 2곳을 가져온다.
        // 병원 데이터 및 영상 데이터를 사용자의 이메일로 보내준다.

        // Front 에 보낼 데이터는 UI 구성시 내부 요소 결정
        AccidentResponseDto responseDto = new AccidentResponseDto();
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(responseDto), AccidentResponseDto.class);
    }
}
