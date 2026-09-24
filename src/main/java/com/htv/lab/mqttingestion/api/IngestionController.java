package com.htv.lab.mqttingestion.api;

import com.htv.lab.mqttingestion.domain.Entities.SensorReading;
import com.htv.lab.mqttingestion.processing.IngestionService;
import com.htv.lab.mqttingestion.repo.SensorReadingRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class IngestionController {
    private final IngestionService ingestion;
    private final SensorReadingRepository readings;

    public IngestionController(IngestionService i, SensorReadingRepository r) {
        ingestion = i;
        readings = r;
    }

    @PostMapping("/simulate")
    ResponseEntity<Void> simulate(@RequestParam String topic, @RequestBody String payload) {
        ingestion.ingest(topic, payload);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/readings/{tenant}/{device}")
    List<SensorReading> readings(@PathVariable String tenant, @PathVariable String device) {
        return readings.findTop100ByTenantIdAndDeviceIdOrderByRecordedAtDesc(tenant, device);
    }
}
