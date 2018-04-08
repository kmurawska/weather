package com.kmurawska.weather.weatheranalyzer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherMessageConsumer implements Runnable {
    private static final Logger LOG = Logger.getLogger(WeatherMessageConsumer.class.getName());
    private static final int TIMEOUT_IN_SECONDS = 1;
    private final Consumer<String, String> consumer;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final String id;
    private final TemperatureMessageProducer temperatureMessageProducer;

    WeatherMessageConsumer(String id) {
        this.id = id;
        this.consumer = createConsumer();
        this.temperatureMessageProducer = TemperatureMessageProducer.create(id + "_" + UUID.randomUUID().toString());
    }

    private Consumer<String, String> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BOOTSTRAP_SERVERS"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, this.id);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return new KafkaConsumer<>(props);
    }

    @Override
    public void run() {
        try {
            subscribe();
            while (!closed.get()) {
                consumer.poll(TIMEOUT_IN_SECONDS * 1000).forEach(this::handleRecord);
            }
        } catch (WakeupException e) {
            if (!closed.get()) throw e;
        } finally {
            consumer.close();
            LOG.log(Level.INFO, "--- Consumer: " + this.id + " has been closed.");
        }
    }

    private void subscribe() {
        consumer.subscribe(Collections.singletonList("current-weather"));
        consumer.subscription().forEach(s -> LOG.log(Level.INFO, "--- Consumer: " + this.id + ": subscribed on topic: " + s));
    }

    private void handleRecord(ConsumerRecord record) {
        CompletableFuture.runAsync(() -> {
            LOG.log(Level.INFO,
                    "--- Message: " + record.key() + "  consumed, " +
                            "offset: " + record.offset() + " " +
                            "partition : " + record.partition() + " " +
                            "topic: " + record.topic()
            );

            JsonObject message = JsonbBuilder.create().fromJson(record.value().toString(), JsonObject.class);

            temperatureMessageProducer.publish(new TemperatureRecord(message));
        });
    }

    public void shutdown() {
        closed.set(true);
        consumer.wakeup();
        LOG.log(Level.INFO, "--- Consumer: " + this.id + " will be closed...");
    }
}