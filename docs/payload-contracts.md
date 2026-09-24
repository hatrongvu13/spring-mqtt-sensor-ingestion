# Payload contracts

Telemetry topic: `iot/acme/device-01/telemetry`
```json
{"messageId":"m-001","sensorType":"temperature","value":28.45,"unit":"C","recordedAt":"2026-09-24T12:00:00Z","tags":{"site":"factory-a"}}
```

State topic: `iot/acme/device-01/state`
```json
{"messageId":"s-001","status":"ONLINE","firmware":"1.4.0","observedAt":"2026-09-24T12:00:00Z"}
```

Rules: message IDs are globally unique in this demo; timestamps are ISO-8601 UTC; non-finite values and demo temperature outside -100..200 are rejected; units and sensor types are normalized to lowercase.
