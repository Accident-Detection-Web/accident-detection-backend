package com.example.accidentdetectionservice.domain.hospital.entity;

import com.example.accidentdetectionservice.domain.user.entity.User;
import com.example.accidentdetectionservice.domain.webflux.dto.AccidentRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.jdi.StringReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Accident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accident_id")
    private Long id;

    @Column(name = "time")
    private String time;

    @Column(name = "latitute")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "address")
    private String address;

    @Column(name = "severity_level")
    private Long severityLevel;

    @Column(name = "severity")
    private String severity;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User receiver;

    @OneToMany(mappedBy = "accident")
    private List<Hospital> list = new ArrayList<>();

    public Accident(String address, AccidentRequestDto requestDto, User receiver) {
        this.address = address;
        this.time = requestDto.getTime();
        this.latitude = requestDto.getLatitude();
        this.longitude = requestDto.getLongitude();
        this.severityLevel = requestDto.getSeverityLevel();
        this.severity = requestDto.getSeverity();
        this.receiver = receiver;
    }
}