# Deployment and operations

## Local
Use Docker Compose for Mosquitto and run the app with Maven. Set `MQTT_ENABLED=false` to test exclusively through `/api/v1/simulate`.

## Production
- Broker: EMQX, HiveMQ or clustered Mosquitto with TLS and per-device credentials.
- Use unique, stable client IDs; persistent sessions; QoS 1 for telemetry unless loss is acceptable.
- Run multiple ingestion instances with shared subscriptions when the broker supports them, for example `$share/ingestors/iot/+/+/telemetry`.
- Replace H2 with PostgreSQL/TimescaleDB for metadata or time-series history; use Flyway.
- Add outbox + Kafka/Pulsar for fan-out to analytics, alerting and digital-twin services.
- Store secrets in Vault/Kubernetes Secrets and rotate broker credentials.
- Add Kubernetes readiness based on broker connectivity and DB health, resource limits, PodDisruptionBudget and graceful shutdown.
- Observe message rate, lag, reconnects, rejected payloads, duplicate ratio, DB latency and aggregation delay.
- Apply payload-size limits, schema versioning, tenant quotas and topic ACLs.
