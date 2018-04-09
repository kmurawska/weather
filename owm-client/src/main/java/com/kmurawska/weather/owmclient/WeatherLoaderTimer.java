package com.kmurawska.weather.owmclient;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
public class WeatherLoaderTimer {
    private static final Logger LOG = Logger.getLogger(WeatherLoaderTimer.class.getName());

    @Inject
    OpenWeatherMapClient openWeatherMapClient;

    @Inject
    WeatherEventProducer weatherEventProducer;

    @Schedule(hour = "*", minute = "*/10", persistent = false)
    void load() {
        try {
            String weather = openWeatherMapClient.loadCurrentWeatherFor("Gdansk");
            weatherEventProducer.publish(weather);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "An error occurred during loading weather data.", e);
        }
    }
}