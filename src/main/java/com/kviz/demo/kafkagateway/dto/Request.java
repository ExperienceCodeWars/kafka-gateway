package com.kviz.demo.kafkagateway.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class Request {
    @NotNull(message = "Can not be null")
    private String messageId;
    @NotNull(message = "Can not be null")
    private String operationType;
    @NotNull(message = "Can not be null")
    private String senderSystemCode;
    @Valid
    private Client client;
}
