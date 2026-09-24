package com.htv.lab.mqttingestion.processing;

import com.htv.lab.mqttingestion.model.Messages.TopicContext;
import org.springframework.stereotype.Component;

@Component
public class TopicParser {
    public TopicContext parse(String topic) {
        String[] p = topic.split("/");
        if (p.length != 4 || !p[0].equals("iot"))
            throw new IllegalArgumentException("Expected topic iot/{tenant}/{device}/{telemetry|state}");
        return new TopicContext(topic, p[1], p[2], p[3]);
    }
}
