package com.kmurawska.weather.temperaturetracker;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.WakeupException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Lock(LockType.READ)
@Startup
public class TemperatureTracker {
    private static final Logger LOG = Logger.getLogger(TemperatureTracker.class.getName());
    private static final int TIMEOUT_IN_SECONDS = 1;
    private static final String TOPIC = "temperature";
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private TemperatureRecordConsumer consumer;
    private Future<?> consumerFuture;

    @Resource
    ManagedExecutorService managedExecutorService;

    @PostConstruct
    public void init() {
        LOG.log(Level.INFO, "--- Starting messages consumption... ---");
        consumerFuture = this.managedExecutorService.submit(this::startMessageConsumption);
    }

    private void startMessageConsumption() {
        LOG.log(Level.INFO, "+++++++++++++ Starting messages consumption 2 ... ---");

        this.consumer = TemperatureRecordConsumer.create();
        try {
            this.consumer.start(TOPIC);
            while (!closed.get()) {
                consumer.getConsumer().poll(TIMEOUT_IN_SECONDS * 1000).forEach(this::handleRecord);
            }
        } catch (WakeupException e) {
            if (!closed.get()) throw e;
        } finally {
            LOG.log(Level.INFO, "--- Consumer: " + consumer.getId() + " has been closed.");
        }
    }

    private void handleRecord(ConsumerRecord record) {
        managedExecutorService.submit(() -> {
            LOG.log(Level.INFO, "--- Message: " + record.key() + "  consumed, " +
                    "offset: " + record.offset() + " " +
                    "partition : " + record.partition() + " " +
                    "topic: " + record.topic());
            TemperatureMeasurement temperature = new TemperatureMeasurement(JsonbBuilder.create().fromJson(record.value().toString(), JsonObject.class));
            LOG.log(Level.INFO, "--- TemperatureMeasurement recorded: " + temperature.toString());
        });
    }

    @PreDestroy
    public void cleanUp() {
        closed.set(true);
        consumer.shutdown();
        consumerFuture = null;
        managedExecutorService.shutdown();
    }
}