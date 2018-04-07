package com.kmurawska.weather.owmclient;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class WeatherMessageProducer {
    public RecordMetadata send(String message) throws ExecutionException, InterruptedException {
        Producer<String, String> producer = createProducer();

        final ProducerRecord<String, String> record = new ProducerRecord<>(System.getenv("TOPICS"), UUID.randomUUID().toString(), message);

        return producer.send(record).get();
    }

    private Producer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BOOTSTRAP_SERVERS"));
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "owm-client");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }
}
