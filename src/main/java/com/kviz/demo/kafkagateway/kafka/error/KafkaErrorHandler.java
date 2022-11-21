package com.kviz.demo.kafkagateway.kafka.error;

import com.kviz.demo.kafkagateway.dto.Request;
import com.kviz.demo.kafkagateway.dto.Response;
import com.kviz.demo.kafkagateway.kafka.producer.KafkaProducerErrorTopic;
import com.kviz.demo.kafkagateway.kafka.producer.KafkaProducerResultTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.ContainerAwareErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaErrorHandler implements ContainerAwareErrorHandler {
    private final KafkaProducerErrorTopic kafkaProducerErrorTopic;
    private final KafkaProducerResultTopic kafkaProducerResultTopic;

    public void handle(Exception thrownException, @Nullable List<ConsumerRecord<?, ?>> records, Consumer<?, ?> consumer, MessageListenerContainer container) {
        this.doSeeks(records, consumer);
        if (!records.isEmpty()) {
            ConsumerRecord<?, ?> record = records.get(0);

            Optional<DeserializationException> deserializationException = isDeserializationException(thrownException);
            Optional<ConstraintViolationException> constraintViolationException = isConstraintViolationException(thrownException);

            processCertainErrors(deserializationException, constraintViolationException, record, thrownException);
            fixOffset(record, consumer, container);
        } else {
            log.debug("Consumer exception - cause: {}", thrownException.getMessage());
        }

    }

    private void doSeeks(List<ConsumerRecord<?, ?>> records, Consumer<?, ?> consumer) {
        Map<TopicPartition, Long> partitions = new LinkedHashMap<>();
        AtomicBoolean first = new AtomicBoolean(true);
        records.forEach((record) -> {
            if (first.get()) {
                partitions.put(new TopicPartition(record.topic(), record.partition()), record.offset() + 1L);
            } else {
                partitions.computeIfAbsent(new TopicPartition(record.topic(), record.partition()), (offset) -> record.offset());
            }
            first.set(false);
        });
        partitions.forEach(consumer::seek);
    }

    private Optional<DeserializationException> isDeserializationException(Exception thrownException) {
        if (thrownException.getClass().equals(DeserializationException.class)) {
            return Optional.of((DeserializationException) thrownException);
        }
        if (thrownException.getCause() != null &&
                DeserializationException.class.equals(thrownException.getCause().getClass())) {
            return Optional.of((DeserializationException) thrownException.getCause());
        }
        return Optional.empty();
    }

    private Optional<ConstraintViolationException> isConstraintViolationException(Exception thrownException) {
        if (thrownException.getClass().equals(ConstraintViolationException.class)) {
            return Optional.of((ConstraintViolationException) thrownException);
        }
        if (thrownException.getCause() != null &&
                ConstraintViolationException.class.equals(thrownException.getCause().getClass())) {
            return Optional.of((ConstraintViolationException) thrownException.getCause());
        }
        return Optional.empty();
    }

    private void processCertainErrors(Optional<DeserializationException> deserializationException,
                                      Optional<ConstraintViolationException> constraintViolationException,
                                      ConsumerRecord<?, ?> record, Exception thrownException) {
        var topic = record.topic();
        var offset = record.offset();
        var partition = record.partition();

        if (deserializationException.isPresent()) {
            DeserializationException exception = deserializationException.get();
            String malformedMessage = new String(exception.getData());
            var errorMsg = String.format("Kafka deserialization error. Skipping message with topic {%s} and offset {%s} - malformed message: {%s} , exception: {%s}",
                    topic, offset, malformedMessage, exception.getRootCause().getMessage());
            log.error(errorMsg);
            kafkaProducerErrorTopic.sendMessage(errorMsg);
        } else if (constraintViolationException.isPresent()) {
            ConstraintViolationException exception = constraintViolationException.get();
            var errorMsg = String.format("Kafka validate DTO error. Skipping message with topic {%s} - offset {%s} - partition {%s} - exception {%s}",
                    topic, offset, partition, exception.getMessage());
            log.error(errorMsg);
            prepareErrorMsg(record, errorMsg);
        } else {
            var errorMsg = String.format("Kafka error. Skipping message with topic {%s} - offset {%s} - partition {%s} - exception {%s}",
                    topic, offset, partition, thrownException.getMessage());
            log.error(errorMsg);
            prepareErrorMsg(record, errorMsg);
        }
    }

    private void prepareErrorMsg(ConsumerRecord<?, ?> record, String errorMsg) {
        var value = record.value();
        log.info("entering prepareErrorMsg: {}", value);
        if (value instanceof Request) {
            Request request = (Request) record.value();

            kafkaProducerResultTopic.sendMessage(new Response()
                    .setOperationType(request.getOperationType())
                    .setOperationStatus("fail")
                    .setMessageId(request.getMessageId())
                    .setErrorMessage(errorMsg));

        }
    }

    private void fixOffset(ConsumerRecord<?, ?> record, Consumer<?, ?> consumer, MessageListenerContainer container) {
        if (container.getContainerProperties().getAckMode().equals(ContainerProperties.AckMode.MANUAL_IMMEDIATE)) {
            if (container.getContainerProperties().isSyncCommits()) {
                Map<TopicPartition, OffsetAndMetadata> offsetToCommit = Collections.singletonMap(
                        new TopicPartition(record.topic(), record.partition()),
                        new OffsetAndMetadata(record.offset() + 1));
                consumer.commitSync(offsetToCommit, container.getContainerProperties().getSyncCommitTimeout());
            }
        }
    }
}
