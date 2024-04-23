package com.example.accidentdetectionservice.domain.mail.util;

import jakarta.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailUtil {

    // 이메일 주소를 String 배열로 변환하는 메서드
    public InternetAddress stringToArray(String emailAddress, String charset)
        throws UnsupportedEncodingException {
        return new InternetAddress(emailAddress, "", charset);
    }
}
