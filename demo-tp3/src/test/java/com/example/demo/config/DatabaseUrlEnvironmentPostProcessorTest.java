package com.example.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseUrlEnvironmentPostProcessorTest {

    private final DatabaseUrlEnvironmentPostProcessor processor = new DatabaseUrlEnvironmentPostProcessor();

    @Test
    void postProcessEnvironment_renderUrl_convertsToCleanJdbcAndExtractsCredentials() {
        ConfigurableEnvironment environment = new StandardEnvironment();
        Map<String, Object> props = new HashMap<>();
        props.put("spring.datasource.url", "postgres://webii_tp3:secreto123@dpg-c123456789-a.oregon-postgres.render.com:5432/webii_tp3");
        environment.getPropertySources().addFirst(new MapPropertySource("testSource", props));

        processor.postProcessEnvironment(environment, new SpringApplication());

        assertEquals("jdbc:postgresql://dpg-c123456789-a.oregon-postgres.render.com:5432/webii_tp3",
                environment.getProperty("spring.datasource.url"));
        assertEquals("webii_tp3", environment.getProperty("spring.datasource.username"));
        assertEquals("secreto123", environment.getProperty("spring.datasource.password"));
    }

    @Test
    void postProcessEnvironment_jdbcUrlWithUserInfo_cleansHostAndExtractsCredentials() {
        ConfigurableEnvironment environment = new StandardEnvironment();
        Map<String, Object> props = new HashMap<>();
        props.put("spring.datasource.url", "jdbc:postgresql://webii_tp3:secreto123@dpg-c123456789-a.oregon-postgres.render.com:5432/webii_tp3");
        environment.getPropertySources().addFirst(new MapPropertySource("testSource", props));

        processor.postProcessEnvironment(environment, new SpringApplication());

        assertEquals("jdbc:postgresql://dpg-c123456789-a.oregon-postgres.render.com:5432/webii_tp3",
                environment.getProperty("spring.datasource.url"));
        assertEquals("webii_tp3", environment.getProperty("spring.datasource.username"));
        assertEquals("secreto123", environment.getProperty("spring.datasource.password"));
    }

    @Test
    void postProcessEnvironment_standardJdbcUrl_doesNotModify() {
        ConfigurableEnvironment environment = new StandardEnvironment();
        Map<String, Object> props = new HashMap<>();
        props.put("spring.datasource.url", "jdbc:postgresql://localhost:5432/webii_tp2");
        environment.getPropertySources().addFirst(new MapPropertySource("testSource", props));

        processor.postProcessEnvironment(environment, new SpringApplication());

        assertEquals("jdbc:postgresql://localhost:5432/webii_tp2",
                environment.getProperty("spring.datasource.url"));
    }
}

