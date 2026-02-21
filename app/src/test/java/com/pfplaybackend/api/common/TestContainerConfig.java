package com.pfplaybackend.api.common;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public class TestContainerConfig {

    public static final MySQLContainer<?> MYSQL = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.30"))
            .withDatabaseName("pfplay_test")
            .withUsername("test")
            .withPassword("test");

    @SuppressWarnings("resource")
    public static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    static {
        MYSQL.start();
        REDIS.start();
    }
}
