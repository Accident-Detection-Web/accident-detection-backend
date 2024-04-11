package com.example.accidentdetectionservice.domain.user.service;

import com.example.accidentdetectionservice.domain.user.dto.MessageResponseDto;
import com.example.accidentdetectionservice.domain.user.dto.SignupRequestDto;
import com.example.accidentdetectionservice.domain.user.entity.User;
import com.example.accidentdetectionservice.domain.user.entity.UserRoleEnum;
import com.example.accidentdetectionservice.domain.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     * @param requestDto
     * @return
     */
    @Transactional
    public ResponseEntity<MessageResponseDto> signup(SignupRequestDto requestDto) {

        log.info("회원 가입");
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 아이디가 존재합니다");
        }

        String email = requestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 이메일이 존재합니다");
        }

        User newUser = new User(username, password, email, UserRoleEnum.USER);
        userRepository.save(newUser);

        MessageResponseDto message = new MessageResponseDto("회원가입을 성공했습니다.", HttpStatus.OK.value());
        return ResponseEntity.status(HttpStatus.OK).body(message);

    }
}
