package com.kmurawska.weather.temperature_tracker.control;

import com.kmurawska.weather.temperature_tracker.cassandra.CassandraConnector;
import com.kmurawska.weather.temperature_tracker.cassandra.KeyspaceInitializer;
import com.kmurawska.weather.temperature_tracker.entity.TemperatureRecordedEvent;

import javax.inject.Inject;

public class TemperatureMeasurementRepository {
    @Inject
    private CassandraConnector cassandraConnector;

    public void save(TemperatureRecordedEvent temperature) {
        String insertStatement = "INSERT INTO " + KeyspaceInitializer.TEMPERATURE_BY_CITY + " (city, temperature_measurement_id, value, recorded_at) " +
                "VALUES ('" + temperature.getCity() + "', " + temperature.getId() + ", " + temperature.getValue() + ", '" + temperature.getRecordedAt() + "');";

        this.cassandraConnector.getSession().execute(insertStatement);
    }
}
