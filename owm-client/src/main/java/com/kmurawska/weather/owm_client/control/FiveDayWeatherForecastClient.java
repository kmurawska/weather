package com.kmurawska.weather.owm_client.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static java.util.stream.Collectors.joining;

public class FiveDayWeatherForecastClient {
    private static final String OPEN_WEATHER_MAP_URL = "http://api.openweathermap.org/data/2.5/forecast?units=metric";
    private static final String API_KEY = "8c797b048309a1d0a3027d4e2df110d2";

    public String requestCurrentWeatherFor(String city) throws IOException {
        URL url = new URL(OPEN_WEATHER_MAP_URL + "&" + "APPID=" + API_KEY + "&q=" + city);

        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return buffer.lines().collect(joining("\n"));
        }
    }
}