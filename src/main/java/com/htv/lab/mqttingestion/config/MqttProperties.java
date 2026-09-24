package com.htv.lab.mqttingestion.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties("app.mqtt")
public record MqttProperties(boolean enabled, String url, String clientId, String username, String password,
                             List<String> topics, List<Integer> qos, boolean automaticReconnect, boolean cleanSession,
                             Duration completionTimeout) {
}
