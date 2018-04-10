package com.kmurawska.weather.temperature_tracker;

import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PrivateFieldsVisibleJsonbConfig {

    public static JsonbConfig privateFieldsVisible() {
        return new JsonbConfig().withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {
            @Override
            public boolean isVisible(Field field) {
                return true;
            }

            @Override
            public boolean isVisible(Method method) {
                return false;
            }
        });
    }
}