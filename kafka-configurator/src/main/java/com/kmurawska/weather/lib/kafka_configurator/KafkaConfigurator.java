package com.kmurawska.weather.lib.kafka_configurator;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import java.io.IOException;
import java.util.Properties;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;

public class KafkaConfigurator {
    private Properties properties;

    @PostConstruct
    void fetchConfiguration() {
        try {
            properties = new Properties();
            properties.put(BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BOOTSTRAP_SERVERS"));
            tryFetchConfiguration();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load properties.", e);
        }
    }

    private Properties tryFetchConfiguration() throws IOException {
        properties.load(KafkaConfigurator.class.getResourceAsStream("/kafka.properties"));
        return properties;
    }

    @Produces
    public Properties exposeKafkaProperties() {
        Properties kafkaProperties = new Properties();
        kafkaProperties.putAll(properties);
        return kafkaProperties;
    }
}