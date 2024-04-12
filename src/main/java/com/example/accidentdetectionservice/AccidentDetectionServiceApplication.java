package com.example.accidentdetectionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@SpringBootApplication
@EnableJpaAuditing
public class AccidentDetectionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccidentDetectionServiceApplication.class, args);
    }

}
