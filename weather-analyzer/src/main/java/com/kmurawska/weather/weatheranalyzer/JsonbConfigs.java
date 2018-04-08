package com.kmurawska.weather.weatheranalyzer;

import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

class JsonbConfigs {
    static final JsonbConfig PRIVATE_FILED_VISIBLE = new JsonbConfig().withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {
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