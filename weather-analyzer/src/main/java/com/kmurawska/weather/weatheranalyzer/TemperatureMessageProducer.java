package com.kmurawska.weather.weatheranalyzer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.UUID.randomUUID;

public class TemperatureMessageProducer {
    private static final Logger LOG = Logger.getLogger(TemperatureMessageProducer.class.getName());
    private static final String TOPIC = "temperature";
    private final Producer<String, String> producer;

    public static TemperatureMessageProducer create(String id) {
        return new TemperatureMessageProducer(createProducer(id));
    }

    private static Producer<String, String> createProducer(String id) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BOOTSTRAP_SERVERS"));
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "temperature-message-producer" + id);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    private TemperatureMessageProducer(Producer<String, String> producer) {
        this.producer = producer;
    }

    public void publish(TemperatureRecord temperatureRecord) {
        final ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, randomUUID().toString(), temperatureRecord.toJson());

        LOG.log(Level.INFO, "--- Message: " + record.key() + " will be sent...");

        producer.send(record, (r, e) -> logProducedMessage(record, r, e));
    }

    private void logProducedMessage(ProducerRecord<String, String> record, RecordMetadata recordMetadata, Exception e) {
        if (e != null) {
            LOG.log(Level.SEVERE, "An error occurred during publishing a message", e);
            throw new RuntimeException(e);
        }

        LOG.log(Level.INFO,
                "--- Message: " + record.key() + " produced, offset: " + recordMetadata.offset() + " " +
                        "partition : " + recordMetadata.partition() + " " +
                        "topic: " + recordMetadata.topic()
        );
    }
}