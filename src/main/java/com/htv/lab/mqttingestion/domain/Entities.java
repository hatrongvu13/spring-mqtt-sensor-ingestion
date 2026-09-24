package com.htv.lab.mqttingestion.domain;

import jakarta.persistence.*;

import java.time.*;

public final class Entities {
    private Entities() {
    }

    @Entity
    @Table(name = "raw_messages", uniqueConstraints = @UniqueConstraint(name = "uk_raw_message", columnNames = "message_id"))
    public static class RawMessage {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(name = "message_id", nullable = false)
        private String messageId;
        private String topic;
        private String tenantId;
        private String deviceId;
        @Lob
        private String payload;
        private String status;
        private String error;
        private Instant receivedAt;

        protected RawMessage() {
        }

        public RawMessage(String mid, String t, String tenant, String device, String p) {
            messageId = mid;
            topic = t;
            tenantId = tenant;
            deviceId = device;
            payload = p;
            status = "RECEIVED";
            receivedAt = Instant.now();
        }

        public void processed() {
            status = "PROCESSED";
        }

        public void rejected(String e) {
            status = "REJECTED";
            error = e;
        }

        public String getMessageId() {
            return messageId;
        }
    }

    @Entity
    @Table(
            name = "sensor_readings",
            indexes = {
                    @Index(
                            name = "idx_reading_device_time",
                            columnList = "device_id, recorded_at"
                    )
            }
    )
    public static class SensorReading {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(
                name = "message_id",
                nullable = false,
                unique = true
        )
        private String messageId;

        @Column(name = "tenant_id", nullable = false)
        private String tenantId;

        @Column(name = "device_id", nullable = false)
        private String deviceId;

        @Column(name = "sensor_type", nullable = false)
        private String sensorType;

        @Column(name = "measurement_value", nullable = false)
        private double value;

        @Column(name = "unit", nullable = false)
        private String unit;

        @Column(name = "recorded_at", nullable = false)
        private Instant recordedAt;

        @Column(name = "received_at", nullable = false)
        private Instant receivedAt;

        @Column(name = "quality", nullable = false)
        private String quality;

        protected SensorReading() {
        }

        public SensorReading(
                String messageId,
                String tenantId,
                String deviceId,
                String sensorType,
                double value,
                String unit,
                Instant recordedAt,
                String quality
        ) {
            this.messageId = messageId;
            this.tenantId = tenantId;
            this.deviceId = deviceId;
            this.sensorType = sensorType;
            this.value = value;
            this.unit = unit;
            this.recordedAt = recordedAt;
            this.receivedAt = Instant.now();
            this.quality = quality;
        }

        public Long getId() {
            return id;
        }

        public String getMessageId() {
            return messageId;
        }

        public String getTenantId() {
            return tenantId;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public String getSensorType() {
            return sensorType;
        }

        public double getValue() {
            return value;
        }

        public String getUnit() {
            return unit;
        }

        public Instant getRecordedAt() {
            return recordedAt;
        }

        public Instant getReceivedAt() {
            return receivedAt;
        }

        public String getQuality() {
            return quality;
        }
    }

    @Entity
    @Table(name = "device_state", uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "device_id"}))
    public static class DeviceState {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(name = "tenant_id")
        private String tenantId;
        @Column(name = "device_id")
        private String deviceId;
        private String firmware;
        private String status;
        private Instant observedAt;

        protected DeviceState() {
        }

        public DeviceState(String t, String d, String f, String s, Instant at) {
            tenantId = t;
            deviceId = d;
            firmware = f;
            status = s;
            observedAt = at;
        }

        public void update(String f, String s, Instant at) {
            firmware = f;
            status = s;
            observedAt = at;
        }
    }

    @Entity
    @Table(name = "sensor_aggregates")
    public static class SensorAggregate {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String tenantId;
        private String deviceId;
        private String sensorType;
        private Instant windowStart;
        private Instant windowEnd;
        private long sampleCount;
        private double minimum;
        private double maximum;
        private double average;

        protected SensorAggregate() {
        }

        public SensorAggregate(String t, String d, String s, Instant ws, Instant we, long c, double min, double max, double avg) {
            tenantId = t;
            deviceId = d;
            sensorType = s;
            windowStart = ws;
            windowEnd = we;
            sampleCount = c;
            minimum = min;
            maximum = max;
            average = avg;
        }
    }
}
