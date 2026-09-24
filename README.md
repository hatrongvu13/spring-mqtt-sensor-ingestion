# Spring MQTT Sensor Ingestion

Mẫu Spring Boot 4 nhận dữ liệu cảm biến liên tục hoặc trạng thái định danh qua MQTT, lưu raw inbox, chống trùng, làm sạch, chuẩn hóa, lưu H2 và tổng hợp theo cửa sổ thời gian. Dự án được tổ chức để mở rộng thành ingestion service trong hệ microservices lớn.

## Stack
Java 17, Spring Boot 4.1.1, Spring Integration MQTT, Eclipse Paho MQTT v3, JPA, H2, Actuator, Mosquitto local.

## Run
```bash
docker compose up -d
mvn clean test
mvn spring-boot:run
```
App: `http://localhost:8083`; broker: `tcp://localhost:1883`; H2 Console: `http://localhost:8083/h2-console`; JDBC URL: `jdbc:h2:file:./data/mqtt-ingestion;MODE=PostgreSQL;AUTO_SERVER=TRUE`, user `sa`, password trống.

## Publish telemetry
```bash
mosquitto_pub -h localhost -p 1883 -q 1 -t 'iot/acme/device-01/telemetry' -m '{"messageId":"m-001","sensorType":"temperature","value":28.45,"unit":"C","recordedAt":"2026-09-24T12:00:00Z","tags":{"site":"factory-a"}}'
```

## Publish state
```bash
mosquitto_pub -h localhost -p 1883 -q 1 -t 'iot/acme/device-01/state' -m '{"messageId":"s-001","status":"ONLINE","firmware":"1.4.0","observedAt":"2026-09-24T12:00:00Z"}'
```

## Test without broker
```bash
MQTT_ENABLED=false mvn spring-boot:run
curl -X POST 'http://localhost:8083/api/v1/simulate?topic=iot/acme/device-01/telemetry' -H 'Content-Type: application/json' -d '{"messageId":"m-002","sensorType":"humidity","value":65.2,"unit":"percent","recordedAt":"2026-09-24T12:01:00Z","tags":{}}'
curl 'http://localhost:8083/api/v1/readings/acme/device-01'
```

## Data flows
- Continuous telemetry: append immutable normalized readings.
- Device state: upsert latest identified state by tenant/device.
- Raw inbox: audit, deduplication and replay foundation.
- Minute aggregation: count/min/max/average from the previous completed minute.

## Design patterns
Channel Adapter, Publish/Subscribe, Strategy Registry, Chain of Responsibility, Transactional Inbox, Repository, Aggregator and DLQ foundation. Details are in `docs/architecture.md`.

## Important cautions
- QoS 1 is at-least-once, so duplicates are normal. Keep stable message IDs and idempotent writes.
- The demo uses one transaction spanning raw and normalized persistence. A true outbox is required before publishing to another broker.
- H2 is appropriate for local/demo, not high-volume durable telemetry.
- Do not perform heavy work on the MQTT callback thread. The project hands work to an `ExecutorChannel`; production needs a bounded executor and overload policy.
- Validate topic ACLs, payload size, clock drift and schema versions.
- `cleanSession=false` requires a stable client ID. Multiple replicas need unique IDs or broker shared subscriptions.

## Documentation
- `docs/architecture.md`
- `docs/sequences.md`
- `docs/payload-contracts.md`
- `docs/deployment.md`
- `docs/upgrade.md`
