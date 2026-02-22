package com.pfplaybackend.api.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class AdminExecutorConfig {

    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService chatSimulationExecutor() {
        return Executors.newScheduledThreadPool(5);
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService reactionSimulationExecutor() {
        return Executors.newFixedThreadPool(10);
    }
}
