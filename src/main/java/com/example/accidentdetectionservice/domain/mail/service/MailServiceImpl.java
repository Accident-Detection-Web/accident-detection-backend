package com.example.accidentdetectionservice.domain.mail.service;

import com.example.accidentdetectionservice.domain.mail.entity.MailEvent;
import com.example.accidentdetectionservice.domain.mail.repository.MailRepository;
import com.example.accidentdetectionservice.domain.mail.util.MailUtil;
import com.example.accidentdetectionservice.domain.user.entity.User;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService{

    private final MailRepository mailRepository;
    private final JavaMailSenderImpl javaMailSender;
    private final MailUtil mailUtil;

    @Value("${spring.mail.username}")
    private static String FROM_ADDRESS;

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
}
