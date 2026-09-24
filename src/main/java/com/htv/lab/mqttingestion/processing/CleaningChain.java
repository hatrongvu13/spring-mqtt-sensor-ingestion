package com.htv.lab.mqttingestion.processing;

import com.htv.lab.mqttingestion.model.Messages.TelemetryMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CleaningChain {
    private final List<DataCleaner> cleaners = List.of(new FiniteCleaner(), new UnitCleaner(), new RangeCleaner());

    public TelemetryMessage clean(TelemetryMessage m) {
        TelemetryMessage x = m;
        for (DataCleaner c : cleaners) x = c.clean(x);
        return x;
    }

    interface DataCleaner {
        TelemetryMessage clean(TelemetryMessage m);
    }

    static class FiniteCleaner implements DataCleaner {
        public TelemetryMessage clean(TelemetryMessage m) {
            if (!Double.isFinite(m.value())) throw new IllegalArgumentException("value must be finite");
            return m;
        }
    }

    static class UnitCleaner implements DataCleaner {
        public TelemetryMessage clean(TelemetryMessage m) {
            return new TelemetryMessage(m.messageId().trim(), m.sensorType().trim().toLowerCase(), m.value(), m.unit().trim().toLowerCase(), m.recordedAt(), m.tags());
        }
    }

    static class RangeCleaner implements DataCleaner {
        public TelemetryMessage clean(TelemetryMessage m) {
            if (m.sensorType().equals("temperature") && (m.value() < -100 || m.value() > 200))
                throw new IllegalArgumentException("temperature out of demo range");
            if (m.recordedAt().isAfter(java.time.Instant.now().plusSeconds(300)))
                throw new IllegalArgumentException("recordedAt too far in future");
            return m;
        }
    }
}
