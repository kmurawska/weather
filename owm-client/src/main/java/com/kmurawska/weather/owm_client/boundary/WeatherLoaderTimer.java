package com.kmurawska.weather.owm_client.boundary;

import com.kmurawska.weather.owm_client.control.CurrentWeatherEventProducer;
import com.kmurawska.weather.owm_client.control.CurrentWeatherClient;
import com.kmurawska.weather.owm_client.entity.WeatherDataLoadedEvent;

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
    CurrentWeatherClient openWeatherMapClient;

    @Inject
    CurrentWeatherEventProducer weatherEventProducer;

    @Schedule(hour = "*", minute = "*/10", persistent = false)
    void load() {
        try {
            String weather = openWeatherMapClient.requestCurrentWeatherFor("Gdansk");
            weatherEventProducer.publish(new WeatherDataLoadedEvent(weather));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "An error occurred during loading weather data.", e);
        }
    }
}