package com.htv.lab.mqttingestion.processing;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.*;
import org.springframework.stereotype.Component;

@Component
public class MqttMessageHandler {
    private final IngestionService service;

    public MqttMessageHandler(IngestionService s) {
        service = s;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handle(Message<String> message) {
        String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC, String.class);
        service.ingest(topic, message.getPayload());
    }
}
