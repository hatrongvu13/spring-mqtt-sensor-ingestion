package com.htv.lab.mqttingestion.repo;

import com.htv.lab.mqttingestion.domain.Entities.DeviceState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceStateRepository extends JpaRepository<DeviceState, Long> {
    Optional<DeviceState> findByTenantIdAndDeviceId(String tenant, String device);
}

