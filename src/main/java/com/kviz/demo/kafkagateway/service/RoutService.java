package com.kviz.demo.kafkagateway.service;

import com.kviz.demo.kafkagateway.dto.Request;
import com.kviz.demo.kafkagateway.exceptions.handlers.UnsupportedOperationHandler;
import com.kviz.demo.kafkagateway.model.OperationTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class RoutService {

    private final UnsupportedOperationHandler handler;
    private final AddService addService;
    private final DeleteService deleteService;
    private final VerifyService verifyService;

    public void defineOperation(Request request, Acknowledgment acknowledgment) {
        log.info(">>> defineOperation");
        if (request.getOperationType().equalsIgnoreCase(OperationTypeEnum.ADD_CLIENT.getType())) {
            addService.addClientOperation(request, acknowledgment);
        } else if (request.getOperationType().equalsIgnoreCase(OperationTypeEnum.VERIFY_CLIENT.getType())) {
            verifyService.verifyClientOperation(request, acknowledgment);
        } else if (request.getOperationType().equalsIgnoreCase(OperationTypeEnum.DELETE_CLIENT.getType())) {
            deleteService.deleteClientOperation(request, acknowledgment);
        } else {
            handler.handleAnUnsupportedOperation(request, acknowledgment);
        }
        log.info("<<< defineOperation");
    }

}
