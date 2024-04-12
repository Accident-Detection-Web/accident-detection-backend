package com.example.accidentdetectionservice.domain.notify.repository;

import com.example.accidentdetectionservice.domain.notify.entity.Notify;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotifyRepository extends JpaRepository<Notify, Long> {

}
