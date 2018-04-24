package com.kmurawska.weather.weather_static_splitter.control;

import com.kmurawska.weather.weather_static_splitter.entity.TemperatureRecordedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import javax.json.JsonObject;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.json.bind.JsonbBuilder.create;

public class CurrentWeatherConsumer implements Runnable {
    private static final String TOPIC = "current-weather";
    private static final Logger LOG = Logger.getLogger(CurrentWeatherConsumer.class.getName());
    private static final int TIMEOUT_IN_SECONDS = 1;
    private static final String GROUP_ID = "CurrentWeatherConsumer";
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final KafkaConsumer<String, String> consumer;
    private final Consumer<TemperatureRecordedEvent> eventHandler;

    CurrentWeatherConsumer(Properties kafkaProperties, Consumer<TemperatureRecordedEvent> eventHandler) {
        this.eventHandler = eventHandler;
        this.consumer = createConsumer(kafkaProperties);
        this.subscribe();
    }

    private KafkaConsumer<String, String> createConsumer(Properties kafkaProperties) {
        kafkaProperties.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        return new KafkaConsumer<>(kafkaProperties);
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
