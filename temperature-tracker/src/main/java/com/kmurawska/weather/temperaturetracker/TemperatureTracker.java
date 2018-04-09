package com.kmurawska.weather.temperaturetracker;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.WakeupException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.json.JsonObject;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.json.bind.JsonbBuilder.create;

@Singleton
@Lock(LockType.READ)
@Startup
public class TemperatureTracker {
    private static final Logger LOG = Logger.getLogger(TemperatureTracker.class.getName());
    private static final int TIMEOUT_IN_SECONDS = 1;
    private static final String TOPIC = "temperature";
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private TemperatureRecordConsumer consumer;

    @Resource
    ManagedExecutorService managedExecutorService;

    @Inject
    TemperatureMeasurementRecordHandler temperatureMeasurementRecordHandler;

    @PostConstruct
    public void init() {
        LOG.log(Level.INFO, "--- Starting messages consumption... ---");
        this.managedExecutorService.submit(this::startMessageConsumption);
    }

    private void startMessageConsumption() {
        this.consumer = TemperatureRecordConsumer.create();
        try {
            this.consumer.subscribe(TOPIC);
            while (!closed.get()) {
                consumer.getConsumer().poll(TIMEOUT_IN_SECONDS * 1000).forEach(this::handleRecord);
            }
        } catch (WakeupException e) {
            if (!closed.get()) throw e;
        } finally {
            LOG.log(Level.INFO, "--- Consumer: " + consumer.getId() + " has been closed.");
        }
    }

    @Asynchronous
    private void handleRecord(ConsumerRecord record) {
        LOG.log(Level.INFO, "--- Message: " + record.key() + "  consumed, " +
                "offset: " + record.offset() + " " +
                "partition : " + record.partition() + " " +
                "topic: " + record.topic());

        temperatureMeasurementRecordHandler.handle(create().fromJson(record.value().toString(), JsonObject.class));
    }

    @PreDestroy
    public void cleanUp() {
        closed.set(true);
        consumer.shutdown();
        managedExecutorService.shutdown();
    }
}