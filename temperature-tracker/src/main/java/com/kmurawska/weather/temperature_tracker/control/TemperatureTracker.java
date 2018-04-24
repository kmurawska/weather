package com.kmurawska.weather.temperature_tracker.control;

import com.kmurawska.weather.temperature_tracker.entity.TemperatureRecordedEvent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Properties;

@Singleton
@Lock(LockType.READ)
@Startup
public class TemperatureTracker {
    @Resource
    ManagedExecutorService managedExecutorService;

    @Inject
    Event<TemperatureRecordedEvent> temperatureRecordedEvent;

    @Inject
    Properties kafkaProperties;

    private TemperatureConsumer temperatureConsumer;

    @PostConstruct
    public void init() {
        temperatureConsumer = new TemperatureConsumer(kafkaProperties, e -> temperatureRecordedEvent.fire(e));
        this.managedExecutorService.submit(temperatureConsumer);
    }

    @PreDestroy
    public void cleanUp() {
        temperatureConsumer.shutdown();
        managedExecutorService.shutdown();
    }
}