package com.kmurawska.weather.weather_static_splitter.control;

import com.kmurawska.weather.weather_static_splitter.entity.TemperatureRecordedEvent;
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

public class CurrentWeatherConsumer implements Runnable {
    private static final String TOPIC = "current-weather";
    private static final Logger LOG = Logger.getLogger(CurrentWeatherConsumer.class.getName());
    private static final int TIMEOUT_IN_SECONDS = 1;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final KafkaConsumer<String, String> consumer;
    private final String id;
    private final Consumer<TemperatureRecordedEvent> eventHandler;

    CurrentWeatherConsumer(Consumer<TemperatureRecordedEvent> eventHandler) {
        this.eventHandler = eventHandler;
        this.id = UUID.randomUUID().toString();
        this.consumer = createConsumer();
        this.subscribe();

        //  this.temperatureMessageProducer = TemperatureRecordedEventProducer.create(id + "_" + UUID.randomUUID().toString());
    }

    private KafkaConsumer<String, String> createConsumer() {
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

    private void subscribe() {
        consumer.subscribe(Collections.singletonList(TOPIC));
        consumer.subscription().forEach(s -> LOG.log(Level.INFO, "--- Consumer: " + this.id + ": subscribed on topic: " + s));
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
            LOG.log(Level.INFO, "--- Consumer: " + this.id + " has been closed.");
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
