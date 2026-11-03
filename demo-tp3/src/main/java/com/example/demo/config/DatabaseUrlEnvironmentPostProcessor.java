package com.example.demo.config;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String url = environment.getProperty("spring.datasource.url");
        if (url == null || url.isBlank()) {
            url = environment.getProperty("DATABASE_URL");
        }

        if (url != null && (url.startsWith("postgres://") || url.startsWith("postgresql://")) && !url.startsWith("jdbc:")) {
            try {
                URI uri = new URI(url);
                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath();

                Map<String, Object> overrides = new HashMap<>();
                overrides.put("spring.datasource.url", "jdbc:postgresql://" + host + ":" + port + path);

                String userInfo = uri.getUserInfo();
                if (userInfo != null) {
                    String[] parts = userInfo.split(":", 2);
                    overrides.put("spring.datasource.username", parts[0]);
                    if (parts.length > 1) {
                        overrides.put("spring.datasource.password", parts[1]);
                    }
                }

                environment.getPropertySources().addFirst(new MapPropertySource("renderDatabaseOverride", overrides));
            } catch (Exception ignored) {
            }
        }
    }
}
