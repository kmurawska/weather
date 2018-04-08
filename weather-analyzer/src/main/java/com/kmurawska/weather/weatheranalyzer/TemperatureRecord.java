package com.kmurawska.weather.weatheranalyzer;

import javax.json.JsonObject;

import static javax.json.bind.JsonbBuilder.newBuilder;

public class TemperatureRecord {
    private final String city;
    private final double value;

    TemperatureRecord(JsonObject jsonObject) {
        this.city = jsonObject.getString("name");
        this.value = jsonObject.getJsonObject("main").getJsonNumber("temp").doubleValue();
    }

    public String toJson() {
        return newBuilder().withConfig(JsonbConfigs.PRIVATE_FILED_VISIBLE).build()
                .toJson(this);
    }
}
