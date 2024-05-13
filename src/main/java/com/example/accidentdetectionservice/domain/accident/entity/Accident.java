package com.example.accidentdetectionservice.domain.accident.entity;

import com.example.accidentdetectionservice.domain.hospital.entity.Hospital;
import com.example.accidentdetectionservice.domain.user.entity.User;
import com.example.accidentdetectionservice.domain.accident.dto.AccidentRequestDto;
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
    private String date;

    @Column(name = "latitute")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "address")
    private String address;

    @Column(name = "sorting")
    private String sorting;

    @Column(name = "accuracy")
    private String accuracy;

    @Lob
    @Column(name = "attach_png")
    private byte[] attachPng;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User receiver;

    @OneToMany(mappedBy = "accident")
    private List<Hospital> list = new ArrayList<>();

    public Accident(byte[] attachPng, String address, AccidentRequestDto requestDto, User receiver) {
        this.address = address;
        this.date = requestDto.getDate();
        this.latitude = requestDto.getLatitude();
        this.longitude = requestDto.getLongitude();
        this.sorting = requestDto.getSorting();
        this.accuracy = requestDto.getAccuracy();
        this.receiver = receiver;
        this.attachPng = attachPng;
    }
}
