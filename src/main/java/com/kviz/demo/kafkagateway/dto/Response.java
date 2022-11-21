package com.kviz.demo.kafkagateway.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;


@Accessors(chain = true)
@Data
public class Response {
    @NotNull(message = "Can not be null")
    private String messageId;
    @NotNull(message = "Can not be null")
    private String operationType;
    @NotNull(message = "Can not be null")
    private String operationStatus;
    private String errorMessage;
}
