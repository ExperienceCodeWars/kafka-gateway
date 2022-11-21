package com.kviz.demo.kafkagateway.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationTypeEnum {
    VERIFY_CLIENT("VERIFY"),
    ADD_CLIENT("ADD"),
    DELETE_CLIENT("DELETE");

    private final String type;

}
