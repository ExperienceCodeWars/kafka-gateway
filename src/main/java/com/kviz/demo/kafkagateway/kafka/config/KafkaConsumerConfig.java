package com.kviz.demo.kafkagateway.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kviz.demo.kafkagateway.dto.Request;
import com.kviz.demo.kafkagateway.kafka.config.properties.KafkaConsumerProperties;
import com.kviz.demo.kafkagateway.kafka.error.KafkaErrorHandler;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {
    private final KafkaConsumerProperties kafkaConsumerProperties;
    private final KafkaErrorHandler errorHandler;

    private final ObjectMapper mapper;
    /**
     * Kafka consumer factory setup - standard factory.
     *
     * @return JSON factory.
     */
    @Bean
    public ConsumerFactory<String, Request> consumerFactory() {
        JsonDeserializer<Request> deserializer = new JsonDeserializer<>(Request.class, mapper, false);
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerProperties.getBootstrapServer());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerProperties.getGroupId());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, 2000L);
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 0);
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 0);

        // key.deserializer
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // value.deserializer
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        // spring.deserializer.key.delegate.class
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, JsonDeserializer.class);

        ErrorHandlingDeserializer<Request> errorHandlingDeserializer
                = new ErrorHandlingDeserializer<>(deserializer);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                errorHandlingDeserializer);
    }

    /**
     * Kafka consumer factory setup - wrapper for concurrency.
     *
     * @return wrapped factory.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Request>
    envelopeDtoKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Request> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setBatchListener(false);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConsumerFactory(consumerFactory());
        factory.setErrorHandler(errorHandler);
        return factory;
    }
}
