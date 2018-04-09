package com.kmurawska.weather.owm_client.entity;

import java.util.UUID;

public class CurrentWeatherDataLoadedEvent {
    private final String trackingId;
    private final String weatherData;

    public CurrentWeatherDataLoadedEvent(String weatherData) {
        this.trackingId = UUID.randomUUID().toString();
        this.weatherData = weatherData;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public String asJson() {
        return weatherData;
    }
}