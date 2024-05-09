package com.example.accidentdetectionservice.domain.accident.repository;

import com.example.accidentdetectionservice.domain.accident.entity.Accident;
import com.example.accidentdetectionservice.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccidentRepository extends JpaRepository<Accident, Long> {


    // 해당 유저의 마지막 Accident 객체를 가져오는 메서드
    Optional<Accident> findTopByReceiverOrderByIdDesc(User user);

    // 해당 User 의 마지막으로 저장된 Accident 객체 가져오기
    Optional<Accident> findTopByReceiverIdOrderByIdDesc(Long userId);

    List<Accident> findAllByReceiver(User user);
}
