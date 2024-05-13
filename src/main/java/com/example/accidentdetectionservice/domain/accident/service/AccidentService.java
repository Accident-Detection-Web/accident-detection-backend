package com.example.accidentdetectionservice.domain.accident.service;

import com.example.accidentdetectionservice.domain.accident.dto.AccidentRequestDto;
import com.example.accidentdetectionservice.domain.accident.entity.Accident;
import com.example.accidentdetectionservice.domain.accident.repository.AccidentRepository;
import com.example.accidentdetectionservice.domain.hospital.repository.HospitalRepository;
import com.example.accidentdetectionservice.domain.mail.entity.MailEvent;
import com.example.accidentdetectionservice.domain.mail.repository.MailRepository;
import com.example.accidentdetectionservice.domain.user.dto.MessageResponseDto;
import com.example.accidentdetectionservice.domain.user.entity.User;
import com.example.accidentdetectionservice.global.kafka.producer.notify.NotifyProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccidentService {

    private final MailRepository mailRepository;
    private final AccidentRepository accidentRepository;
    private final HospitalRepository hospitalRepository;
    private final ReverseGeocodingService reverseGeocodingService;
    private final NotifyProducerService notifyProducerService;

    /**
     * @apiNote
     * @param requestDto
     * @param user
     * @return
     */
//    @NeedNotify
    @Transactional
    public MessageResponseDto processFileAndData(byte[] image, AccidentRequestDto requestDto, User user)  {

        notifyProducerService.sendAccidentDetectionNotification(user);

        createAccidentEvent(image, requestDto, user);

//        createMailEvent(image, requestDto, user, accidentEvent); // (3) 2번이 순서에 맞으려면 api 로직을 따로 만들어야 됨

        return new MessageResponseDto("이미지 파일 및 사고 데이터 수신, Accident 객체 생성 성공", HttpStatus.OK.value());
    }

    private void createAccidentEvent(byte[] image, AccidentRequestDto requestDto, User user) {
        try {
            String address = reverseGeocodingService.getAddress(Double.parseDouble(requestDto.getLatitude()), Double.parseDouble(requestDto.getLongitude()));

            accidentRepository.save(new Accident(image, address, requestDto, user));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    private void createMailEvent(byte[] image, AccidentRequestDto requestDto, User user, Accident accident) {
//        try {
//            String toAddress = user.getEmail();
//            String subject = "[Accident Detection] 요청하신 데이터 입니다.";
//
//            // 해당 accident 로 부터 병원 데이터 모두 보내주기 + 정확도, 클래스, 시간
//            String content = "<p>안녕하세요.</p><p>" + " 병원 데이터 입니다. </p><p>감사합니다.</p>";
//
//            mailRepository.save(new MailEvent(toAddress, subject, content, image, user));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }


}
