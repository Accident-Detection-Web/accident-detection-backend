package com.example.accidentdetectionservice.domain.notify.repository;

import com.example.accidentdetectionservice.domain.notify.entity.SseEmitterEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SseEmitterRepository extends JpaRepository<SseEmitterEntity, String> {
    List<SseEmitterEntity> findByUsername(String username);

}
