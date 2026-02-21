package com.pfplaybackend.api.common;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", TestContainerConfig.MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", TestContainerConfig.MYSQL::getUsername);
        registry.add("spring.datasource.password", TestContainerConfig.MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.data.redis.host", TestContainerConfig.REDIS::getHost);
        registry.add("spring.data.redis.port", () -> TestContainerConfig.REDIS.getMappedPort(6379));
    }

    @Autowired
    protected EntityManager entityManager;

    @Transactional
    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
