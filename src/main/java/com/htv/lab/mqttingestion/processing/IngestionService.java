package com.htv.lab.mqttingestion.processing;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.htv.lab.mqttingestion.domain.Entities.RawMessage;
import com.htv.lab.mqttingestion.model.Messages.InboundEnvelope;
import com.htv.lab.mqttingestion.repo.RawMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class IngestionService {
    private final TopicParser topics;
    private final ObjectMapper json;
    private final RawMessageRepository raw;
    private final Map<String, MessageProcessor> processors;

    public IngestionService(TopicParser t, ObjectMapper j, RawMessageRepository r, List<MessageProcessor> p) {
        topics = t;
        json = j;
        raw = r;
        processors = p.stream().collect(Collectors.toUnmodifiableMap(MessageProcessor::kind, Function.identity()));
    }

    @Transactional
    public void ingest(String topic, String payload) {
        var c = topics.parse(topic);
        String messageId = extractId(payload);
        if (raw.existsByMessageId(messageId)) return;
        var inbox = raw.save(new RawMessage(messageId, topic, c.tenantId(), c.deviceId(), payload));
        try {
            var processor = Optional.ofNullable(processors.get(c.kind())).orElseThrow(() -> new IllegalArgumentException("unsupported message kind: " + c.kind()));
            processor.process(new InboundEnvelope(c, payload));
            inbox.processed();
        } catch (Exception e) {
            inbox.rejected(e.getMessage());
            throw new IllegalArgumentException("message rejected: " + e.getMessage(), e);
        }
    }

    private String extractId(String payload) {
        try {
            JsonNode n = json.readTree(payload);
            String id = n.path("messageId").asText();
            if (id.isBlank()) throw new IllegalArgumentException("messageId is required");
            return id;
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid JSON: " + e.getMessage(), e);
        }
    }
}