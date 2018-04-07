package com.kmurawska.weather.owmclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

public class WeatherDataLoader {
    private static final String OPEN_WEATHER_MAP_URL = "http://api.openweathermap.org/data/2.5/weather?id=524901";
    private static final String API_KEY = "8c797b048309a1d0a3027d4e2df110d2";

    public String loadCurrentWeatherFor(String city) throws IOException {
        URL url = new URL(OPEN_WEATHER_MAP_URL + "&" + "APPID=" + API_KEY + "&q=" + city);

        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }
}