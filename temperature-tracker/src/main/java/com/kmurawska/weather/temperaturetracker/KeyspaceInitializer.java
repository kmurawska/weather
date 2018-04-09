package com.kmurawska.weather.temperaturetracker;

import com.datastax.driver.core.Session;

class KeyspaceInitializer {
    private static final String KEYSPACE = "temperature_recorder";
    static final String TEMPERATURE_BY_CITY = "temperature_recorder.temperature_by_city";
    private final Session session;

    KeyspaceInitializer(Session session) {
        this.session = session;
    }

    void init() {
        createKeyspace();
        createTables();
    }

    private void createKeyspace() {
        this.session.execute(
                "CREATE KEYSPACE IF NOT EXISTS " + KEYSPACE + " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1' }"
        );
    }

    private void createTables() {
        this.session.execute(
                "CREATE TABLE IF NOT EXISTS " + TEMPERATURE_BY_CITY + " (" +
                        "city TEXT," +
                        "temperature_measurement_id UUID," +
                        "value DECIMAL," +
                        "recorded_at TIMESTAMP, " +
                        "PRIMARY KEY ((city), recorded_at, temperature_measurement_id)" +
                        ") WITH CLUSTERING ORDER BY (recorded_at DESC);"
        );
    }
}