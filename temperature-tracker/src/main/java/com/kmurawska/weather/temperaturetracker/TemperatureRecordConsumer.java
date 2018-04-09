package com.kmurawska.weather.temperaturetracker;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TemperatureRecordConsumer {
    private static final Logger LOG = Logger.getLogger(TemperatureRecordConsumer.class.getName());
    private final String id;
    private final Consumer<String, String> consumer;

    public static TemperatureRecordConsumer create() {
        String id = UUID.randomUUID().toString();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BOOTSTRAP_SERVERS"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, id);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return new TemperatureRecordConsumer(id, new KafkaConsumer<>(props));
    }

    private TemperatureRecordConsumer(String id, Consumer<String, String> consumer) {
        this.id = id;
        this.consumer = consumer;
    }

     void start(String topic) {
        consumer.subscribe(Collections.singletonList(topic));
        consumer.subscription().forEach(s -> LOG.log(Level.INFO, "--- Consumer: " + id + ": subscribed on topic: " + s));
    }

    public String getId() {
        return id;
    }

    public Consumer<String, String> getConsumer() {
        return consumer;
    }

    public void shutdown() {
        consumer.wakeup();
        LOG.log(Level.INFO, "--- Consumer: " + this.id + " will be closed...");
    }
}
