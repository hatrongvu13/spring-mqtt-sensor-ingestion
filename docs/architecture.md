# Architecture

## Pipeline
Broker -> Spring Integration inbound adapter -> ExecutorChannel -> topic parser -> inbox/idempotency -> processor strategy -> cleaning chain -> JPA -> scheduled window aggregation.

## Patterns
- **Publish/Subscribe**: MQTT decouples devices and consumers.
- **Channel Adapter**: Spring Integration isolates MQTT transport from business processing.
- **Strategy + Factory/Registry**: message kind selects telemetry or state processor; add a processor without changing ingestion orchestration.
- **Chain of Responsibility**: finite-value, normalization and range cleaners run in order.
- **Transactional Inbox**: raw message and processing status support idempotency, audit and replay foundations.
- **Repository**: persistence boundaries hide JPA.
- **Aggregator**: scheduled time-window statistics produce count/min/max/average.
- **Dead-letter foundation**: rejected raw records retain error text. Production should publish to a real DLQ topic after transaction completion.

## MSA boundary
Keep MQTT ingestion focused on protocol termination, validation and durable normalized events. In a large microservice system, publish normalized events to Kafka/Pulsar rather than letting every business service subscribe directly to device topics. Use an outbox table to avoid dual-write loss.

## Topic convention
`iot/{tenantId}/{deviceId}/{telemetry|state}`. Tenant and device identity come from the topic, while `messageId` comes from payload for deduplication.
