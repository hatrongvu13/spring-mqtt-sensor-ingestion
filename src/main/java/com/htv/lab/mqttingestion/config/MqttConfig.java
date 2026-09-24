package com.htv.lab.mqttingestion.config;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.*;
import org.springframework.integration.mqtt.core.*;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.*;

@Configuration
@EnableConfigurationProperties(MqttProperties.class)
public class MqttConfig {
    @Bean
    MessageChannel mqttInputChannel() {
        var executor = new java.util.concurrent.ThreadPoolExecutor(4, 4, 0L, java.util.concurrent.TimeUnit.MILLISECONDS, new java.util.concurrent.ArrayBlockingQueue<>(500), new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        return new ExecutorChannel(executor);
    }

    @Bean
    MessageChannel mqttErrorChannel() {
        return new DirectChannel();
    }

    @Bean
    @ConditionalOnProperty(name = "app.mqtt.enabled", havingValue = "true")
    DefaultMqttPahoClientFactory mqttClientFactory(MqttProperties p) {
        var o = new MqttConnectOptions();
        o.setServerURIs(new String[]{p.url()});
        o.setAutomaticReconnect(p.automaticReconnect());
        o.setCleanSession(p.cleanSession());
        if (p.username() != null && !p.username().isBlank()) o.setUserName(p.username());
        if (p.password() != null && !p.password().isBlank()) o.setPassword(p.password().toCharArray());
        var f = new DefaultMqttPahoClientFactory();
        f.setConnectionOptions(o);
        return f;
    }

    @Bean
    @ConditionalOnProperty(name = "app.mqtt.enabled", havingValue = "true")
    MqttPahoMessageDrivenChannelAdapter mqttInbound(MqttProperties p, DefaultMqttPahoClientFactory f) {
        var a = new MqttPahoMessageDrivenChannelAdapter(p.clientId(), f, p.topics().toArray(String[]::new));
        for (int i = 0; i < p.topics().size(); i++) a.setQos(p.qos().size() == 1 ? p.qos().get(0) : p.qos().get(i));
        a.setOutputChannel(mqttInputChannel());
        a.setErrorChannel(mqttErrorChannel());
        a.setCompletionTimeout(p.completionTimeout().toMillis());
        return a;
    }

    @ServiceActivator(inputChannel = "mqttErrorChannel")
    void mqttError(Message<?> m) {
        org.slf4j.LoggerFactory.getLogger(MqttConfig.class).error("MQTT adapter error: {}", m);
    }
}
