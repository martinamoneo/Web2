package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.net.URI;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSourceProperties dataSourceProperties(
            @Value("${spring.datasource.url}") String rawUrl,
            @Value("${spring.datasource.username:}") String rawUsername,
            @Value("${spring.datasource.password:}") String rawPassword) {

        DataSourceProperties properties = new DataSourceProperties();

        if (rawUrl != null && (rawUrl.startsWith("postgres://") || (rawUrl.startsWith("postgresql://") && !rawUrl.startsWith("jdbc:")))) {
            try {
                URI uri = new URI(rawUrl);
                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath();

                String userInfo = uri.getUserInfo();
                if (userInfo != null) {
                    String[] parts = userInfo.split(":", 2);
                    properties.setUsername(parts[0]);
                    if (parts.length > 1) {
                        properties.setPassword(parts[1]);
                    }
                } else {
                    properties.setUsername(rawUsername);
                    properties.setPassword(rawPassword);
                }

                properties.setUrl("jdbc:postgresql://" + host + ":" + port + path);
                return properties;
            } catch (Exception ignored) {
            }
        }

        properties.setUrl(rawUrl);
        properties.setUsername(rawUsername);
        properties.setPassword(rawPassword);
        return properties;
    }
}
