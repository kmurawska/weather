package com.kmurawska.weather.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

@Path("weather")
public class WeatherResource {
    private static final String API_KEY = "8c797b048309a1d0a3027d4e2df110d2";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get() throws IOException {
        URL url = new URL("http://api.openweathermap.org/data/2.5/weather?id=524901&APPID=" + API_KEY + "&q=Gdansk");

        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }
}
