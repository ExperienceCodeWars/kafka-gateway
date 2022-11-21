package com.kviz.demo.kafkagateway.kafka.consumer;

import com.kviz.demo.kafkagateway.dto.Request;
import com.kviz.demo.kafkagateway.service.RoutService;
import com.kviz.demo.kafkagateway.validators.DtoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final RoutService routService;
    private final DtoValidator dtoValidator;

    @KafkaListener(topics = "#{'${ms.gateway.kafka.consumer.topic}'.split(',')}", concurrency = "1",
            containerFactory = "envelopeDtoKafkaListenerContainerFactory")
    public void listenerTopic(ConsumerRecord<String, Request> record, Acknowledgment acknowledgment) {
        log.info("Kafka consumer incoming message : {}", record.value());
        var request = record.value();
        dtoValidator.validCntsEnvelopeDto(request);
        routService.defineOperation(request, acknowledgment);
    }
}
