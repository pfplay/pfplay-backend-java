package com.pfplaybackend.api.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.jackson.ModelResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.LOWER_CAMEL_CASE;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper mapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(LOWER_CAMEL_CASE);

        //swagger
        ModelConverters.getInstance().addConverter(new ModelResolver(objectMapper));
        return objectMapper;
    }
}
