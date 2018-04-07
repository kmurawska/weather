package com.kmurawska.weather.owmclient;

import org.apache.kafka.clients.producer.RecordMetadata;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

@Path("weather")
public class WeatherResource {
    private static final Logger LOG = Logger.getLogger(WeatherResource.class.getName());

    @Inject
    WeatherMessageProducer weatherMessageProducer;

    @Inject
    WeatherDataLoader weatherDataLoader;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        try {
            String weather = weatherDataLoader.loadCurrentWeatherFor("Gdansk");
            RecordMetadata recordMetadata = weatherMessageProducer.send(weather);
            LOG.log(Level.INFO, recordMetadata.toString());
            return Response.ok(weather).build();
        } catch (IOException | InterruptedException | ExecutionException e) {
            LOG.log(Level.SEVERE, "Unable to load current weather data.", e);
            return Response.status(INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}