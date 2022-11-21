package com.kviz.demo.kafkagateway.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaProducerErrorTopic {

    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${ms.gateway.kafka.producer.errorTopic}")
    private String errorTopic;


    public KafkaProducerErrorTopic(@Qualifier("errorTemplate") KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String msg) {
        kafkaTemplate.send(errorTopic, msg)
                .addCallback(
                        result -> log.info("Message send to topic {} : {}", errorTopic, msg),
                        ex -> log.error("Failed to send message", ex)
                );
        kafkaTemplate.flush();
    }
}
