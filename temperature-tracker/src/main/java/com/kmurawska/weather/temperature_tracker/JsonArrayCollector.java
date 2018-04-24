package com.kmurawska.weather.temperature_tracker;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.util.stream.Collector;

public class JsonArrayCollector {
    public static <T> Collector<JsonObject, ?, JsonArray> toJsonArray() {
        return Collector.of(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add, JsonArrayBuilder::build);
    }
}
