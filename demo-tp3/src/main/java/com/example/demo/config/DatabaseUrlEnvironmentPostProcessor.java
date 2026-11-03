package com.example.demo.config;

import org.springframework.boot.env.EnvironmentPostProcessor;
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
        String raw = environment.getProperty("spring.datasource.url");
        if (raw == null || raw.isBlank()) {
            raw = environment.getProperty("DATABASE_URL");
        }
        if (raw == null || raw.isBlank()) {
            return;
        }

        // Si la URL contiene credenciales incrustadas (@) o no empieza con "jdbc:"
        if (raw.contains("@") || raw.startsWith("postgres://") || (raw.startsWith("postgresql://") && !raw.startsWith("jdbc:"))) {
            try {
                String s = raw.trim();
                if (s.startsWith("jdbc:")) {
                    s = s.substring(5);
                }
                if (s.startsWith("postgres://")) {
                    s = "postgresql://" + s.substring("postgres://".length());
                }

                URI uri = new URI(s);
                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath() != null ? uri.getPath() : "";

                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
                String query = uri.getQuery();
                if (query != null && !query.isBlank()) {
                    jdbcUrl += "?" + query;
                }

                Map<String, Object> overrides = new HashMap<>();
                overrides.put("spring.datasource.url", jdbcUrl);
                overrides.put("SPRING_DATASOURCE_URL", jdbcUrl);

                String userInfo = uri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    overrides.put("spring.datasource.username", parts[0]);
                    overrides.put("SPRING_DATASOURCE_USERNAME", parts[0]);
                    overrides.put("spring.datasource.password", parts[1]);
                    overrides.put("SPRING_DATASOURCE_PASSWORD", parts[1]);
                }

                environment.getPropertySources().addFirst(new MapPropertySource("renderDatabaseOverride", overrides));
                System.out.println("[DatabaseUrlEnvironmentPostProcessor] Successfully configured JDBC URL: " + jdbcUrl);
            } catch (Exception e) {
                System.err.println("[DatabaseUrlEnvironmentPostProcessor] Warning: failed to parse database URL: " + e.getMessage());
            }
        }
    }
}
