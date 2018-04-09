package com.kmurawska.weather.owmclient.entity;

import java.util.UUID;

import static com.kmurawska.weather.owmclient.PrivateFieldsVisibleJsonbConfig.privateFieldsVisible;
import static javax.json.bind.JsonbBuilder.newBuilder;

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
        return newBuilder().withConfig(privateFieldsVisible()).build()
                .toJson(this);
    }
}