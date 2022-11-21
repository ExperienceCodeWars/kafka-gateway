package com.kviz.demo.kafkagateway.kafka.producer;

import com.kviz.demo.kafkagateway.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerResultTopic {
    private final KafkaTemplate<String, Response> kafkaTemplate;

    @Value("${ms.gateway.kafka.producer.resultTopic}")
    private String resultTopic;

    public void sendDtoToKafkaAndAcknowledge(Response response, Acknowledgment acknowledgment) {
        try {
            sendMessage(response);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }
    public void sendMessage(Response msg) {
        kafkaTemplate.send(resultTopic, msg)
                .addCallback(
                        result -> log.info("Message send to topic {} : {}", resultTopic, msg),
                        ex -> log.error("Failed to send message", ex)
                );
        kafkaTemplate.flush();
    }
}
