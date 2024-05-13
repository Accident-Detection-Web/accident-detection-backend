package com.example.accidentdetectionservice.domain.mail.service;

import com.example.accidentdetectionservice.domain.accident.entity.Accident;
import com.example.accidentdetectionservice.domain.accident.repository.AccidentRepository;
import com.example.accidentdetectionservice.domain.hospital.dto.HospitalResponseDto;
import com.example.accidentdetectionservice.domain.hospital.entity.Hospital;
import com.example.accidentdetectionservice.domain.hospital.repository.HospitalRepository;
import com.example.accidentdetectionservice.domain.mail.entity.MailEvent;
import com.example.accidentdetectionservice.domain.mail.repository.MailRepository;
import com.example.accidentdetectionservice.domain.mail.util.MailUtil;
import com.example.accidentdetectionservice.domain.user.dto.MessageResponseDto;
import com.example.accidentdetectionservice.domain.user.entity.User;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MailServiceImpl implements MailService{

    private final MailRepository mailRepository;
    private final JavaMailSenderImpl javaMailSender;
    private final MailUtil mailUtil;
    private final AccidentRepository accidentRepository;
    private final HospitalRepository hospitalRepository;

    @Value("${spring.mail.username}")
    private static String FROM_ADDRESS;

    @Transactional
    @Override
    public void sendMail(User receiver) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MailEvent mailEvent = mailRepository.findByReceiver(receiver);


        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            InternetAddress toAddress = mailUtil.stringToArray(mailEvent.getToAddress(), "UTF-8");

            mimeMessageHelper.setSubject(MimeUtility.encodeText(mailEvent.getSubject(), "UTF-8", "B"));
            mimeMessageHelper.setText(mailEvent.getContent(), true);
            mimeMessageHelper.setFrom(new InternetAddress(FROM_ADDRESS, FROM_ADDRESS, "UTF-8"));
            mimeMessageHelper.setTo(toAddress);

            ByteArrayDataSource dataSource = new ByteArrayDataSource(
                mailEvent.getAttachPng(), "image/png");
            mimeMessageHelper.addAttachment("image.png", dataSource);

            javaMailSender.send(mimeMessage);
            mailRepository.delete(mailEvent);

        } catch (Exception e) {
            throw new MailSendException("MailServiceImpl.sendMail :: FAILED");
        }
    }

    @Transactional
    @Override
    public MessageResponseDto createMailEventEntity(User receiver) {
        try {

            Accident lastAccident = accidentRepository.findTopByReceiverIdOrderByIdDesc(receiver.getId()).orElseThrow(
                    () -> new IllegalArgumentException("해당 유저의 사고 객체가 없습니다. ")
            );

            List<Hospital> hospitals = hospitalRepository.findAllByAccident(lastAccident);


            String toAddress = receiver.getEmail();
            String subject = "[Accident Detection] 요청하신 데이터 입니다.";

            String content = hospitals.stream()
                    .map(hospital -> String.format("<div>병원이름 : %s, 전화번호 : %s</div>", hospital.getName(), hospital.getTel()))
                    .collect(Collectors.joining("\n"));

            mailRepository.save(new MailEvent(toAddress, subject, content, lastAccident.getAttachPng(), receiver));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new MessageResponseDto("mailEvent 객체 생성 성공", HttpStatus.OK.value());

    }
}
