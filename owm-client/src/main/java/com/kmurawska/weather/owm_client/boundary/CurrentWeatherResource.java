package com.kmurawska.weather.owm_client.boundary;

import com.kmurawska.weather.owm_client.control.CurrentWeatherEventProducer;
import com.kmurawska.weather.owm_client.control.CurrentWeatherClient;
import com.kmurawska.weather.owm_client.entity.WeatherDataLoadedEvent;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

@Path("current-weather")
public class CurrentWeatherResource {
    private static final Logger LOG = Logger.getLogger(CurrentWeatherResource.class.getName());

    @Inject
    CurrentWeatherClient openWeatherMapClient;

    @Inject
    CurrentWeatherEventProducer eventProducer;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        try {
            String weather = openWeatherMapClient.requestCurrentWeatherFor("Gdansk");
            eventProducer.publish(new WeatherDataLoadedEvent(weather));
            return Response.ok(weather).build();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "An error occurred during loading weather data.", e);
            return Response.status(INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}