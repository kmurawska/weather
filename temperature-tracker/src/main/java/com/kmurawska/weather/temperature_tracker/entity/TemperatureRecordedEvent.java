package com.kmurawska.weather.temperature_tracker.entity;

import com.datastax.driver.core.Row;

import javax.json.JsonObject;
import java.time.Instant;
import java.util.UUID;

import static javax.json.Json.createObjectBuilder;

public class TemperatureRecordedEvent {
    private final UUID id;
    private final String city;
    private final double value;
    private final Instant recordedAt;

    public TemperatureRecordedEvent(String trackingId, JsonObject jsonObject) {
        this.id = UUID.fromString(trackingId);
        this.city = jsonObject.getString("city");
        this.value = jsonObject.getJsonNumber("value").doubleValue();
        this.recordedAt = Instant.parse(jsonObject.getString("recordedAtUtc"));
    }

    public TemperatureRecordedEvent(Row row) {
        this.id = row.getUUID("temperature_measurement_id");
        this.city = row.getString("city");
        this.value = row.getDecimal("value").doubleValue();
        this.recordedAt = row.getTimestamp("recorded_at").toInstant();
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
        return "TemperatureRecordedEvent{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", value=" + value +
                ", recordedAt=" + recordedAt +
                '}';
    }

    public JsonObject asJson() {
        return createObjectBuilder()
                .add("temperatureMeasurementId", this.id.toString())
                .add("city", this.city)
                .add("value", this.value)
                .add("recordedAt", this.recordedAt.toString())
                .build();
    }
}