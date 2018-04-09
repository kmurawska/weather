package com.kmurawska.weather.temperaturetracker;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class TemperatureMeasurementRepository {
    @Inject
    private CassandraConnector cassandraConnector;

    public void save(TemperatureMeasurement temperature) {
        String insertStatement = "INSERT INTO " + KeyspaceInitializer.TEMPERATURE_BY_CITY + " (city, temperature_measurement_id, value, recorded_at) " +
                "VALUES ('" + temperature.getCity() + "', " + temperature.getId() + ", " + temperature.getValue() + ", '" + temperature.getRecordedAt() + "');";

        this.cassandraConnector.getSession().execute(insertStatement);
    }
}
