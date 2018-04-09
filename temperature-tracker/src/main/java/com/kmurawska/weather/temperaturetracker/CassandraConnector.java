package com.kmurawska.weather.temperaturetracker;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.extras.codecs.jdk8.InstantCodec;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Lock(LockType.READ)
@Startup
public class CassandraConnector {
    private Cluster cluster;
    private Session session;

    @PostConstruct
    public void init() {
        cluster = Cluster.builder()
                .addContactPoints(System.getenv("CASSANDRA_CONTACT_POINTS").split(","))
                .build();
        cluster.getConfiguration().getCodecRegistry().register(InstantCodec.instance);
        session = cluster.connect();
        new KeyspaceInitializer(this.session).init();
    }

    public Session getSession() {
        return this.session;
    }

    @PreDestroy
    public void close() {
        if (null != session) {
            session.close();
        }
        if (null != cluster) {
            cluster.close();
        }
    }
}