package com.kmurawska.weather.temperature_tracker.control;

import com.kmurawska.weather.temperature_tracker.cassandra.CassandraConnector;
import com.kmurawska.weather.temperature_tracker.entity.TemperatureRecordedEvent;

import javax.inject.Inject;

import java.util.List;

import static com.kmurawska.weather.temperature_tracker.cassandra.KeyspaceInitializer.TEMPERATURE_BY_CITY;
import static java.util.stream.Collectors.toList;

public class TemperatureMeasurementRepository {
    @Inject
    private CassandraConnector cassandraConnector;

    public List<TemperatureRecordedEvent> all() {
        return this.cassandraConnector.getSession().execute("SELECT * FROM " + TEMPERATURE_BY_CITY).all()
                .stream()
                .map(TemperatureRecordedEvent::new)
                .collect(toList());
    }

    public void save(TemperatureRecordedEvent temperature) {
        String insertStatement = "INSERT INTO " + TEMPERATURE_BY_CITY + " (city, temperature_measurement_id, value, recorded_at) " +
                "VALUES ('" + temperature.getCity() + "', " + temperature.getId() + ", " + temperature.getValue() + ", '" + temperature.getRecordedAt() + "');";

        this.cassandraConnector.getSession().execute(insertStatement);
    }
}
