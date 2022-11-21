package com.kviz.demo.kafkagateway.dto;

import lombok.Data;
import lombok.experimental.Accessors;

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
    @NotNull(message = "Can not be null")
    private String clientType;
    private String clientFullName;
    @NotNull(message = "Can not be null")
    private String account;
}
