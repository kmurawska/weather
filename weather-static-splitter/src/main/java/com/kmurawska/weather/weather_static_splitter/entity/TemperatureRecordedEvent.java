package com.kmurawska.weather.weather_static_splitter.entity;

import javax.json.JsonObject;

import static com.kmurawska.weather.weather_static_splitter.PrivateFieldsVisibleJsonbConfig.privateFieldsVisible;
import static javax.json.bind.JsonbBuilder.newBuilder;

public class TemperatureRecordedEvent {
    private final String trackingId, city;
    private final double value;

    public TemperatureRecordedEvent(String trackingId, JsonObject jsonObject) {
        this.trackingId = trackingId;
        this.city = jsonObject.getString("name");
        this.value = jsonObject.getJsonObject("main").getJsonNumber("temp").doubleValue();
    }

    public String asJson() {
        return newBuilder().withConfig(privateFieldsVisible()).build()
                .toJson(this);
    }

    public String getTrackingId() {
        return trackingId;
    }
}
