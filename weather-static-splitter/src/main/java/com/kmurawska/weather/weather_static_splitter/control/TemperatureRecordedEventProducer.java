package com.kmurawska.weather.weather_static_splitter.control;

import com.kmurawska.weather.weather_static_splitter.entity.TemperatureRecordedEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TemperatureRecordedEventProducer {
    private static final Logger LOG = Logger.getLogger(TemperatureRecordedEventProducer.class.getName());
    private static final String TOPIC = "temperature";
    private Producer<String, String> producer;

    @PostConstruct
    private void init() {
        producer = createProducer();
        producer.initTransactions();
    }

    private static Producer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BOOTSTRAP_SERVERS"));
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    void publish(TemperatureRecordedEvent event) {
        final ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, event.getTrackingId(), event.asJson());

        try {
            producer.beginTransaction();
            LOG.log(Level.INFO, "--- Publishing event: " + record.key());
            producer.send(record);
            producer.commitTransaction();
        } catch (ProducerFencedException e) {
            producer.close();
        } catch (KafkaException e) {
            producer.abortTransaction();
        }
    }

    @PreDestroy
    public void close() {
        producer.close();
    }
}