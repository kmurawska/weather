package com.kmurawska.weather.owmclient;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.UUID.randomUUID;

public class WeatherEventProducer {
    private static final Logger LOG = Logger.getLogger(WeatherEventProducer.class.getName());
    private static final String TOPIC = "current-weather";
    private String producerId;
    private Producer<String, String> producer;

    @PostConstruct
    private void init() {
        producerId = UUID.randomUUID().toString();
        producer = createProducer();
    }

    private Producer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BOOTSTRAP_SERVERS"));
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "owm-client_" + producerId);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    public void publish(String message) {
        final ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, randomUUID().toString(), message);

        LOG.log(Level.INFO, "--- Message: " + record.key() + " will be sent...");

        producer.send(record, (r, e) -> logEvent(record, r, e));
    }

    private void logEvent(ProducerRecord<String, String> record, RecordMetadata recordMetadata, Exception e) {
        if (e != null) {
            LOG.log(Level.SEVERE, "An error occurred during publishing a message", e);
            throw new RuntimeException(e);
        }

        LOG.log(Level.INFO,
                "--- Message published: " + record.key() + " produced, offset: " + recordMetadata.offset() + " " +
                        "partition : " + recordMetadata.partition() + " " +
                        "topic: " + recordMetadata.topic()
        );
    }

    @PreDestroy
    public void close() {
        producer.close();
    }
}