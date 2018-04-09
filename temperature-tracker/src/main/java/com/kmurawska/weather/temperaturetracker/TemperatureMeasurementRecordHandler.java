package com.kmurawska.weather.temperaturetracker;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class TemperatureMeasurementRecordHandler {
    private static final Logger LOG = Logger.getLogger(TemperatureRecordConsumer.class.getName());

    @Inject
    TemperatureMeasurementRepository temperatureMeasurementRepository;

    @Asynchronous
    public void handle(JsonObject record) {
        TemperatureMeasurement temperature = new TemperatureMeasurement(record);

        temperatureMeasurementRepository.save(temperature);

        LOG.log(Level.INFO, "--- TemperatureMeasurement " + temperature.getId() + " recorded.");
    }
}