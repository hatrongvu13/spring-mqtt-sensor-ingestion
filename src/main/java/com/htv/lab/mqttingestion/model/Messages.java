package com.htv.lab.mqttingestion.model;

import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.Map;

public final class Messages {
    private Messages() {
    }

    public record TelemetryMessage(@NotBlank String messageId, @NotBlank String sensorType, @NotNull Double value,
                                   @NotBlank String unit, @NotNull Instant recordedAt, Map<String, String> tags) {
    }

    public record StateMessage(@NotBlank String messageId, @NotBlank String status, String firmware,
                               @NotNull Instant observedAt) {
    }

    public record TopicContext(String topic, String tenantId, String deviceId, String kind) {
    }

    public record InboundEnvelope(TopicContext context, String payload) {
    }
}

