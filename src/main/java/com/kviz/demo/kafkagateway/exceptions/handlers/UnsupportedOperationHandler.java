package com.kviz.demo.kafkagateway.exceptions.handlers;

import com.kviz.demo.kafkagateway.dto.Request;
import com.kviz.demo.kafkagateway.dto.Response;
import com.kviz.demo.kafkagateway.kafka.producer.KafkaProducerResultTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UnsupportedOperationHandler {
    private final KafkaProducerResultTopic producer;

    public void handleAnUnsupportedOperation(Request request, Acknowledgment acknowledgment) {
        var operationType = request.getOperationType();
        log.error("Exception, invalid operation type: {} where messageId: {}", operationType, request.getMessageId());
        var response = new Response()
                .setMessageId(request.getMessageId())
                .setOperationType(request.getOperationType())
                .setErrorMessage("Exception, invalid operation type:" + operationType);
        producer.sendDtoToKafkaAndAcknowledge(response, acknowledgment);
    }
}
