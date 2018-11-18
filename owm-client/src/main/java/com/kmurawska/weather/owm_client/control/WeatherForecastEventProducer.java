package com.kmurawska.weather.owm_client.control;

import com.kmurawska.weather.owm_client.entity.WeatherDataLoadedEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.ProducerFencedException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.kafka.clients.producer.ProducerConfig.TRANSACTIONAL_ID_CONFIG;

public class WeatherForecastEventProducer {
    private static final Logger LOG = Logger.getLogger(WeatherForecastEventProducer.class.getName());
    private static final String TOPIC = "weather-forecast";
    private Producer<String, String> producer;

    @Inject
    Properties kafkaProperties;

    @PostConstruct
    private void init() {
        producer = createProducer();
        producer.initTransactions();
    }

    private Producer<String, String> createProducer() {
        kafkaProperties.put(TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString());
        return new KafkaProducer<>(kafkaProperties);
    }

    public void publish(WeatherDataLoadedEvent event) {
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