package com.htv.lab.mqttingestion.repo;

import com.htv.lab.mqttingestion.domain.Entities.SensorAggregate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorAggregateRepository extends JpaRepository<SensorAggregate, Long> {
}
