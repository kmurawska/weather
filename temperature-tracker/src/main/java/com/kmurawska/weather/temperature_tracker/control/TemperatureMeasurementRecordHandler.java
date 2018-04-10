package com.kmurawska.weather.temperature_tracker.control;

import com.kmurawska.weather.temperature_tracker.entity.TemperatureRecordedEvent;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class TemperatureMeasurementRecordHandler {
    private static final Logger LOG = Logger.getLogger(TemperatureMeasurementRecordHandler.class.getName());

    @Inject
    TemperatureMeasurementRepository repository;

    public void apply(@Observes TemperatureRecordedEvent temperature) {
        repository.save(temperature);

        LOG.log(Level.INFO, "--- TemperatureRecordedEvent " + temperature.getId() + " recorded.");
    }
}