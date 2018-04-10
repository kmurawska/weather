package com.kmurawska.weather.temperature_tracker.entity;

import javax.json.JsonObject;
import java.time.Instant;
import java.util.UUID;

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
}