package com.example.accidentdetectionservice.domain.hospital.entity;

import com.example.accidentdetectionservice.domain.accident.entity.Accident;
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


    @Column(name = "hospital_name")
    private String name;

    @Column(name = "hospital_tel")
    private String tel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accident_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Accident accident;


    public Hospital(String name, String tel, Accident accident) {
        this.name = name;
        this.tel = tel;
        this.accident = accident;
    }
}
