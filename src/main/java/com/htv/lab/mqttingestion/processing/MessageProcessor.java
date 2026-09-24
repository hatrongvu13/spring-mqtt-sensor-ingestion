package com.htv.lab.mqttingestion.processing;

import com.htv.lab.mqttingestion.model.Messages.InboundEnvelope;

public interface MessageProcessor {
    String kind();

    void process(InboundEnvelope envelope) throws Exception;
}