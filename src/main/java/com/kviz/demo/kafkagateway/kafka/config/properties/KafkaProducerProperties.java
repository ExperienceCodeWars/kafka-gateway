package com.kviz.demo.kafkagateway.kafka.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ms.gateway.kafka.producer")
public class KafkaProducerProperties {
    private String bootstrapServer;
    private String topic;
}
