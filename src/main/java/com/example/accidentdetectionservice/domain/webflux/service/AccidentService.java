package com.example.accidentdetectionservice.domain.webflux.service;

import com.example.accidentdetectionservice.domain.hospital.entity.Accident;
import com.example.accidentdetectionservice.domain.hospital.repository.AccidentRepository;
import com.example.accidentdetectionservice.domain.mail.entity.MailEvent;
import com.example.accidentdetectionservice.domain.mail.repository.MailRepository;
import com.example.accidentdetectionservice.domain.notify.annotation.NeedNotify;
import com.example.accidentdetectionservice.domain.user.entity.User;
import com.example.accidentdetectionservice.domain.webflux.dto.AccidentRequestDto;
import com.example.accidentdetectionservice.domain.webflux.dto.AccidentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccidentService {

    private final MailRepository mailRepository;
    private final AccidentRepository accidentRepository;
    private final ReverseGeocodingService reverseGeocodingService;


    /**
     * @apiNote 해당 User 의 MailEvent 객체를 만든다.
     * @param requestDto
     * @param user
     * @return
     */
    @Transactional
    public Mono<AccidentResponseDto> processAccidentData(AccidentRequestDto requestDto, User user)  {

        createMailEvent(requestDto, user);

        createAccidentEvent(requestDto, user);

        AccidentResponseDto responseDto = new AccidentResponseDto(requestDto);

        return Mono.just(responseDto);
    }

    private void createAccidentEvent(AccidentRequestDto requestDto, User user) {
        try {
            String address = reverseGeocodingService.getAddress(Double.parseDouble(requestDto.getLatitude()), Double.parseDouble(requestDto.getLongitude()));

            accidentRepository.save(new Accident(address, requestDto, user));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createMailEvent(AccidentRequestDto requestDto, User user) {
        try {
            String toAddress = user.getEmail();
            String subject = "[Accident Detection] 요청하신 데이터 입니다.";
            String content = "<p>안녕하세요.</p><p>" + " 병원 데이터 입니다. </p><p>감사합니다.</p>";
//        byte[] attachPng = requestDto.getPngData().getBytes();

            mailRepository.save(new MailEvent(toAddress, subject, content, null, user));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NeedNotify
    public User sendNotifyClient(User user){
        return user;
    }


}
