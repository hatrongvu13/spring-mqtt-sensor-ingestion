package com.htv.lab.mqttingestion.processing;

import tools.jackson.databind.ObjectMapper;
import com.htv.lab.mqttingestion.domain.Entities.*;
import com.htv.lab.mqttingestion.model.Messages.*;
import com.htv.lab.mqttingestion.repo.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

public final class Processors {
    private Processors() {
    }

    @Component
    public static class TelemetryProcessor implements MessageProcessor {
        private final ObjectMapper json;
        private final SensorReadingRepository repo;
        private final CleaningChain cleaning;

        public TelemetryProcessor(ObjectMapper j, SensorReadingRepository r, CleaningChain c) {
            json = j;
            repo = r;
            cleaning = c;
        }

        public String kind() {
            return "telemetry";
        }

        @Transactional
        public void process(InboundEnvelope e) throws Exception {
            var m = cleaning.clean(json.readValue(e.payload(), TelemetryMessage.class));
            repo.save(new SensorReading(m.messageId(), e.context().tenantId(), e.context().deviceId(), m.sensorType(), m.value(), m.unit(), m.recordedAt(), "CLEAN"));
        }
    }

    @Component
    public static class StateProcessor implements MessageProcessor {
        private final ObjectMapper json;
        private final DeviceStateRepository repo;

        public StateProcessor(ObjectMapper j, DeviceStateRepository r) {
            json = j;
            repo = r;
        }

        public String kind() {
            return "state";
        }

        @Transactional
        public void process(InboundEnvelope e) throws Exception {
            var m = json.readValue(e.payload(), StateMessage.class);
            var s = repo.findByTenantIdAndDeviceId(e.context().tenantId(), e.context().deviceId()).orElseGet(() -> new DeviceState(e.context().tenantId(), e.context().deviceId(), m.firmware(), m.status(), m.observedAt()));
            s.update(m.firmware(), m.status(), m.observedAt());
            repo.save(s);
        }
    }
}
