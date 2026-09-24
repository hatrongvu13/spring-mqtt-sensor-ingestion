package com.htv.lab.mqttingestion.repo;

import com.htv.lab.mqttingestion.domain.Entities.RawMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawMessageRepository extends JpaRepository<RawMessage, Long> {
    boolean existsByMessageId(String id);
}

