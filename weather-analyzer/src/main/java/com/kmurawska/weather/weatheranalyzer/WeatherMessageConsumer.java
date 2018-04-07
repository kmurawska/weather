package com.kmurawska.weather.weatheranalyzer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherMessageConsumer implements Runnable {
    private static final Logger LOG = Logger.getLogger(WeatherMessageConsumer.class.getName());
    private static final int TIMEOUT_IN_SECONDS = 5;
    private final Consumer<String, String> consumer;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final String id;

    WeatherMessageConsumer(String id) {
        this.id = id;
        this.consumer = createConsumer();
    }

    private Consumer<String, String> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BOOTSTRAP_SERVERS"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "weather-analyzer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return new KafkaConsumer<>(props);
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(Collections.singletonList(System.getenv("TOPICS")));
            consumer.subscription().forEach(s -> LOG.log(Level.INFO, "--- Consumer: " + this.id + ": subscribed on topic: " + s + " ---"));
            while (!closed.get()) {
                consumer.poll(TIMEOUT_IN_SECONDS * 1000).forEach(this::logMessage);
            }
        } catch (WakeupException e) {
            if (!closed.get()) throw e;
        } finally {
            consumer.close();
            LOG.log(Level.INFO, "--- Consumer: " + this.id + " has been closed. ---");
        }
    }

    private void logMessage(ConsumerRecord record) {
        LOG.log(Level.INFO, "--- " + "Consumer:" + this.id + "received message: " + record.value() + " ---");
    }

    public void shutdown() {
        closed.set(true);
        consumer.wakeup();
        LOG.log(Level.INFO, "--- Consumer: " + this.id + " will be closed... ---");
    }
}