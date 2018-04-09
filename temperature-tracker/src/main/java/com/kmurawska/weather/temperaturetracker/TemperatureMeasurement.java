package com.kmurawska.weather.temperaturetracker;

import com.datastax.driver.core.utils.UUIDs;

import javax.json.JsonObject;
import java.time.Instant;
import java.util.UUID;

public class TemperatureMeasurement {
    private final UUID id;
    private final String city;
    private final double value;
    private final Instant recordedAt;

    TemperatureMeasurement(JsonObject jsonObject) {
        this.id = UUIDs.random();
        this.city = jsonObject.getString("city");
        this.value = jsonObject.getJsonNumber("value").doubleValue();
        this.recordedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public double getValue() {
        return value;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    @Override
    public String toString() {
        return "TemperatureMeasurement{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", value=" + value +
                ", recordedAt=" + recordedAt +
                '}';
    }
}
