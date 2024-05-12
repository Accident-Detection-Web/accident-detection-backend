package com.example.accidentdetectionservice.domain.user.controller;

import com.example.accidentdetectionservice.domain.user.dto.MessageResponseDto;
import com.example.accidentdetectionservice.domain.user.dto.SignupRequestDto;
import com.example.accidentdetectionservice.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;


    @Operation(summary = "회원 가입 API", description = "...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                content = {@Content(schema = @Schema(implementation = MessageResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PostMapping("/sign-up")
    public ResponseEntity<MessageResponseDto> signup(@RequestBody SignupRequestDto requestDto) {
        return userService.signup(requestDto);
    }

}
