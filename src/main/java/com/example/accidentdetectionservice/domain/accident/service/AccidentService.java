package com.example.accidentdetectionservice.domain.accident.service;

import com.example.accidentdetectionservice.domain.accident.entity.Accident;
import com.example.accidentdetectionservice.domain.accident.repository.AccidentRepository;
import com.example.accidentdetectionservice.domain.mail.entity.MailEvent;
import com.example.accidentdetectionservice.domain.mail.repository.MailRepository;
import com.example.accidentdetectionservice.domain.notify.annotation.NeedNotify;
import com.example.accidentdetectionservice.domain.user.dto.MessageResponseDto;
import com.example.accidentdetectionservice.domain.user.entity.User;
import com.example.accidentdetectionservice.domain.accident.dto.AccidentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    public MessageResponseDto processFileAndData(byte[] image, AccidentRequestDto requestDto, User user)  {

        createMailEvent(image, requestDto, user);

        createAccidentEvent(requestDto, user);

        return new MessageResponseDto("파일 및 사고 데이터 수신 성공", HttpStatus.OK.value());
    }

    private void createAccidentEvent(AccidentRequestDto requestDto, User user) {
        try {
            String address = reverseGeocodingService.getAddress(Double.parseDouble(requestDto.getLatitude()), Double.parseDouble(requestDto.getLongitude()));

            accidentRepository.save(new Accident(address, requestDto, user));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createMailEvent(byte[] image, AccidentRequestDto requestDto, User user) {
        try {
            String toAddress = user.getEmail();
            String subject = "[Accident Detection] 요청하신 데이터 입니다.";
            String content = "<p>안녕하세요.</p><p>" + " 병원 데이터 입니다. </p><p>감사합니다.</p>";

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
