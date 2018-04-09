package com.kmurawska.weather.weatheranalyzer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Lock(LockType.READ)
@Startup
public class WeatherAnalyzer {
    private static final Logger LOG = Logger.getLogger(WeatherMessageConsumer.class.getName());

    @Resource
    ManagedExecutorService managedExecutorService;

    private WeatherMessageConsumer consumer;

    @PostConstruct
    public void init() {
        LOG.log(Level.INFO, "--- Starting consumers... ---");
        consumer = new WeatherMessageConsumer(UUID.randomUUID().toString());
        this.managedExecutorService.submit(consumer);
    }

    @PreDestroy
    public void cleanUp() {
        consumer.shutdown();
        managedExecutorService.shutdown();
    }
}