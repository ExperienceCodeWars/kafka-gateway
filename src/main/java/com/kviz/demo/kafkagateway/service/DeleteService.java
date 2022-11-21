package com.kviz.demo.kafkagateway.service;

import com.kviz.demo.kafkagateway.dto.Request;
import com.kviz.demo.kafkagateway.dto.Response;
import com.kviz.demo.kafkagateway.kafka.producer.KafkaProducerResultTopic;
import com.kviz.demo.kafkagateway.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteService {

    private final ClientRepository clientRepository;
    private final KafkaProducerResultTopic producer;

    public void deleteClientOperation(Request request, Acknowledgment acknowledgment) {
        var listClient = clientRepository.findAll().stream()
                .filter(client -> client.getAccount().equals(request.getAccount()) && client.getType().equals(request.getClientType()))
                .collect(Collectors.toList());
        var response = new Response()
                .setOperationType(request.getOperationType())
                .setMessageId(request.getMessageId());

        if (listClient.size() > 1) {
            response = new Response()
                    .setOperationStatus("fail")
                    .setErrorMessage("multiple clients");
        } else if (listClient.isEmpty()) {
            response = new Response()
                    .setOperationStatus("fail")
                    .setErrorMessage("client is missing");
        } else {
            clientRepository.deleteByTypeAndAccount(request.getClientType(), request.getAccount());
            response = new Response()
                    .setOperationStatus("success");
        }

        producer.sendDtoToKafkaAndAcknowledge(response, acknowledgment);
    }
}
