package com.kmurawska.weather.temperature_tracker.control;

import com.kmurawska.weather.temperature_tracker.entity.TemperatureRecordedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.json.JsonObject;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.json.bind.JsonbBuilder.create;

public class TemperatureConsumer implements Runnable {
    private static final String TOPIC = "temperature";
    private static final Logger LOG = Logger.getLogger(TemperatureConsumer.class.getName());
    private static final int TIMEOUT_IN_SECONDS = 1;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final KafkaConsumer<String, String> consumer;
    private final Consumer<TemperatureRecordedEvent> eventHandler;
    private static final String GROUP_ID = UUID.randomUUID().toString();

    TemperatureConsumer(Consumer<TemperatureRecordedEvent> eventHandler) {
        this.eventHandler = eventHandler;
        this.consumer = createConsumer();
        this.subscribe();
    }

    private KafkaConsumer<String, String> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BOOTSTRAP_SERVERS"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return new KafkaConsumer<>(props);
    }

    private void subscribe() {
        consumer.subscribe(Collections.singletonList(TOPIC));
        consumer.subscription().forEach(s -> LOG.log(Level.INFO, "--- Consumer: " + GROUP_ID + ": subscribed on topic: " + s));
    }

    @Override
    public void run() {
        try {
            while (!closed.get()) {
                consume();
            }
        } catch (WakeupException e) {
            if (!closed.get()) throw e;
        } finally {
            consumer.close();
            LOG.log(Level.INFO, "--- Consumer: " + GROUP_ID + " has been closed.");
        }
    }

    private void consume() {
        ConsumerRecords<String, String> records = consumer.poll(TIMEOUT_IN_SECONDS * 1000);
        records.forEach(r -> {
            JsonObject event = create().fromJson(r.value(), JsonObject.class);
            eventHandler.accept(new TemperatureRecordedEvent(r.key(), event));
        });

        consumer.commitSync();
    }

    void shutdown() {
        closed.set(true);
        consumer.wakeup();
    }
}