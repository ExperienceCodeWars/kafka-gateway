package com.kviz.demo.kafkagateway.service;

import com.kviz.demo.kafkagateway.dto.Request;
import com.kviz.demo.kafkagateway.dto.Response;
import com.kviz.demo.kafkagateway.kafka.producer.KafkaProducerResultTopic;
import com.kviz.demo.kafkagateway.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class VerifyService {

    private final ClientRepository clientRepository;
    private final KafkaProducerResultTopic producer;

    public void verifyClientOperation(Request request, Acknowledgment acknowledgment) {
        Optional.ofNullable(clientRepository.findByTypeAndAccount(request.getClientType(), request.getAccount()))
                .ifPresentOrElse(client -> producer.sendDtoToKafkaAndAcknowledge(new Response()
                                .setMessageId(request.getMessageId())
                                .setOperationStatus("success")
                                .setOperationType(request.getOperationType()), acknowledgment),
                        () -> producer.sendDtoToKafkaAndAcknowledge(new Response()
                                .setOperationType(request.getOperationType())
                                .setErrorMessage("verification failed")
                                .setErrorMessage("")
                                .setOperationStatus("fail"), acknowledgment));
    }
}
