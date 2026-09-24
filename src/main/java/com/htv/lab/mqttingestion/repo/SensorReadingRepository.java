package com.htv.lab.mqttingestion.repo;

import com.htv.lab.mqttingestion.domain.Entities.SensorReading;
import org.springframework.data.jpa.repository.*;

import java.time.Instant;
import java.util.List;

public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
    List<SensorReading> findTop100ByTenantIdAndDeviceIdOrderByRecordedAtDesc(String tenant, String device);

    List<SensorReading>
    findByRecordedAtGreaterThanEqualAndRecordedAtLessThan(
            Instant windowStart,
            Instant windowEnd
    );
}
