# Extension and upgrade roadmap

1. Add `schemaVersion` and a versioned deserializer registry.
2. Split cleaners into source-specific profiles and configurable calibration rules.
3. Add an outbox entity and relay normalized events to Kafka/Pulsar.
4. Replace scheduled aggregation with stream processing when windows, lateness and event-time watermarks become important.
5. Add real DLQ/replay APIs, quarantine retention and operator audit.
6. Add per-tenant partitioning and authorization.
7. Replace `ddl-auto` with Flyway before production.
8. Move from H2 to PostgreSQL/TimescaleDB or a dedicated time-series store after measuring write volume and query patterns.
9. Evaluate MQTT 5 for reason codes, user properties, expiry and richer operational behavior.
10. Keep Spring Boot on a supported 4.x patch; test Spring Integration and Paho compatibility before every upgrade.

For high throughput, avoid one database transaction per tiny sample. Consider bounded micro-batches, JDBC batch writes, partitioned consumers and backpressure. Preserve idempotency because QoS 1 can redeliver.
