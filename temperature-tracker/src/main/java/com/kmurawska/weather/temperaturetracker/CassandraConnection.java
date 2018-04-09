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
public class CassandraConnection {
    private static final String[] CONTACT_POINTS = {"cassandra-node-1", "cassandra-node-2", "cassandra-node-3"};
    private Cluster cluster;
    private Session session;

    @PostConstruct
    public void init() {
        cluster = Cluster.builder()
                .addContactPoints(CONTACT_POINTS)
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