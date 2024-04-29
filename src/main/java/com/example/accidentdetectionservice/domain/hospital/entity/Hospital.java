package com.example.accidentdetectionservice.domain.hospital.entity;

import com.example.accidentdetectionservice.domain.user.entity.User;
import com.sun.jdi.StringReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_id")
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



}
