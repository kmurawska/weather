package com.kmurawska.weather.weather_static_splitter.control;

import com.kmurawska.weather.weather_static_splitter.entity.TemperatureRecordedEvent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@Singleton
@Lock(LockType.READ)
@Startup
public class WeatherStaticSplitter {

    @Resource
    ManagedExecutorService managedExecutorService;

    @Inject
    Event<TemperatureRecordedEvent> temperatureRecordedEvent;

    @Inject
    TemperatureRecordedEventProducer kafkaEventProducer;

    private CurrentWeatherConsumer consumer;

    @PostConstruct
    public void init() {
        consumer = new CurrentWeatherConsumer(e -> temperatureRecordedEvent.fire(e));
        this.managedExecutorService.submit(consumer);
    }

    public void apply(@Observes TemperatureRecordedEvent event) {
        kafkaEventProducer.publish(event);
    }

    @PreDestroy
    public void cleanUp() {
        consumer.shutdown();
        managedExecutorService.shutdown();
    }
}