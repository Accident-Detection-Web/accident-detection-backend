package com.example.accidentdetectionservice.global.webflux.service;

import com.example.accidentdetectionservice.domain.notify.annotation.NeedNotify;
import com.example.accidentdetectionservice.domain.user.entity.User;
import com.example.accidentdetectionservice.global.webflux.dto.AccidentRequestDto;
import com.example.accidentdetectionservice.global.webflux.dto.AccidentResponseDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AccidentService {

    public Mono<AccidentResponseDto> processAccidentData(AccidentRequestDto requestDto) {
        // 받은 데이터를 처리하고, 처리 결과를 Mono로 반환하는 비즈니스 로직을 구현합니다.
        AccidentResponseDto responseDto = new AccidentResponseDto(requestDto);
        // 예시로서 단순히 빈 응답 DTO를 반환합니다.
        return Mono.just(responseDto);
    }

    @NeedNotify
    public User sendNotifyClient(User user){
        return user;
    }


}
