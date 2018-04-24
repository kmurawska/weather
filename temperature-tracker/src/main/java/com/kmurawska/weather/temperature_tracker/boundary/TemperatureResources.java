package com.kmurawska.weather.temperature_tracker.boundary;

import com.kmurawska.weather.temperature_tracker.control.TemperatureMeasurementRepository;
import com.kmurawska.weather.temperature_tracker.entity.TemperatureRecordedEvent;

import javax.inject.Inject;
import javax.json.stream.JsonCollectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

import static javax.ws.rs.core.Response.ok;

@Path("temperature")
public class TemperatureResources {
    private static final Logger LOG = Logger.getLogger(TemperatureResources.class.getName());

    @Inject
    TemperatureMeasurementRepository repository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        return ok(repository.all().stream().map(TemperatureRecordedEvent::asJson)
                .collect(JsonCollectors.toJsonArray())).build();

    }
}
