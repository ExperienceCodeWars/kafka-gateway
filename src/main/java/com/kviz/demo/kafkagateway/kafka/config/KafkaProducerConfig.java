package com.kviz.demo.kafkagateway.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kviz.demo.kafkagateway.dto.Response;
import com.kviz.demo.kafkagateway.kafka.config.properties.KafkaProducerProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {
    private final KafkaProducerProperties kafkaProducerProperties;
    private final ObjectMapper mapper;

    @Bean
    public ProducerFactory<String, Response> producerFactory() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerProperties.getBootstrapServer());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        properties.put(ProducerConfig.ACKS_CONFIG, "1");

        DefaultKafkaProducerFactory<String, Response> producerFactory =
                new DefaultKafkaProducerFactory<>(properties);
        producerFactory.setValueSerializer(new JsonSerializer<>(mapper));

        return producerFactory;
    }

    @Bean
    public KafkaTemplate<String, Response> kafkaTemplate(ProducerFactory<String, Response> factory) {
        return new KafkaTemplate<>(factory);
    }

    @Bean("errorProducerFactory")
    public ProducerFactory<String, String> producerErrorFactory() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerProperties.getBootstrapServer());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.ACKS_CONFIG, "1");

        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean("errorTemplate")
    @Qualifier("errorProducerFactory")
    public KafkaTemplate<String, String> kafkaErrorTemplate(ProducerFactory<String, String> factory) {
        return new KafkaTemplate<>(factory);
    }
}
