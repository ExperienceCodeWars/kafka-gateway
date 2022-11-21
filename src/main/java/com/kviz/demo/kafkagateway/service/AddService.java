package com.kviz.demo.kafkagateway.service;

import com.kviz.demo.kafkagateway.dto.Request;
import com.kviz.demo.kafkagateway.dto.Response;
import com.kviz.demo.kafkagateway.kafka.producer.KafkaProducerResultTopic;
import com.kviz.demo.kafkagateway.model.Client;
import com.kviz.demo.kafkagateway.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddService {
    private final ClientRepository clientRepository;
    private final KafkaProducerResultTopic producer;

    public void addClientOperation(Request request, Acknowledgment acknowledgment) {
        log.info(">>> addClientOperation");
        Optional.ofNullable(clientRepository.findByTypeAndAccount(request.getClient().getClientType().getCodeName(),
                        request.getClient().getAccountNumber()))
                .ifPresentOrElse(client -> producer.sendDtoToKafkaAndAcknowledge(
                                new Response()
                                        .setErrorMessage("client already exists")
                                        .setOperationStatus("fail")
                                        .setOperationType(request.getOperationType())
                                        .setMessageId(request.getMessageId()), acknowledgment),
                        () -> {
                            clientRepository.save(new Client()
                                    .setAccount(request.getClient().getAccountNumber())
                                    .setFullName(request.getClient().getClientFullName())
                                    .setType(request.getClient().getClientType().getCodeName()));
                            producer.sendDtoToKafkaAndAcknowledge(new Response()
                                    .setOperationType(request.getOperationType())
                                    .setOperationStatus("success")
                                    .setMessageId(request.getMessageId()), acknowledgment);
                        });
        log.info("<<< addClientOperation");
    }
}
