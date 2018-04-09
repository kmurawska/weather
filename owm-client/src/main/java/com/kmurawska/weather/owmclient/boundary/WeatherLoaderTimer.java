package com.kmurawska.weather.owmclient.boundary;

import com.kmurawska.weather.owmclient.control.CurrentWeatherEventProducer;
import com.kmurawska.weather.owmclient.control.OpenWeatherMapClient;
import com.kmurawska.weather.owmclient.entity.CurrentWeatherDataLoadedEvent;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
/*
@Startup
*/
public class WeatherLoaderTimer {
    private static final Logger LOG = Logger.getLogger(WeatherLoaderTimer.class.getName());

    @Inject
    OpenWeatherMapClient openWeatherMapClient;

    @Inject
    CurrentWeatherEventProducer weatherEventProducer;

    @Schedule(hour = "*", minute = "*/10", persistent = false)
    void load() {
        try {
            String weather = openWeatherMapClient.requestCurrentWeatherFor("Gdansk");
            weatherEventProducer.publish(new CurrentWeatherDataLoadedEvent(weather));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "An error occurred during loading weather data.", e);
        }
    }
}