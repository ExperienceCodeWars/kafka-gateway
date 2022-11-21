package com.kviz.demo.kafkagateway.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class Client {
    @NotNull(message = "Can not be null")
    private ClientType clientType;
    private String clientFullName;
    @NotNull(message = "Can not be null")
    private String accountNumber;
    @NotNull(message = "Can not be null")
    private String activeStatus;
    private String inn;
}
