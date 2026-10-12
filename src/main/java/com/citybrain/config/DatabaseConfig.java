package com.citybrain.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url:}")
    private String configuredUrl;

    @Value("${spring.datasource.username:}")
    private String configuredUsername;

    @Value("${spring.datasource.password:}")
    private String configuredPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        // Priority 1: Environment variable DB_URL
        // Priority 2: Spring configuration property
        String rawUrl = System.getenv("DB_URL");
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = configuredUrl;
        }

        String url = (rawUrl != null) ? rawUrl.trim() : "";

        // Reject missing or unresolved template placeholders
        if (url.isEmpty() || url.startsWith("${") || url.equalsIgnoreCase("${DB_URL}")) {
            throw new IllegalStateException(
                "CRITICAL: Production database URL is missing or set to an unresolved placeholder: '" + url + "'. " +
                "Please configure a valid Aiven MySQL JDBC URL (e.g. jdbc:mysql://HOST:PORT/defaultdb?sslMode=REQUIRED) " +
                "in your Render Dashboard under Environment Variable 'DB_URL'."
            );
        }

        String username = System.getenv("DB_USERNAME");
        if (username == null || username.isBlank() || username.startsWith("${")) {
            username = configuredUsername;
        }

        String password = System.getenv("DB_PASSWORD");
        if (password == null || password.isBlank() || password.startsWith("${")) {
            password = configuredPassword;
        }

        // Support for Aiven URI format: mysql://user:password@host:port/database?sslMode=REQUIRED
        if (url.startsWith("mysql://")) {
            try {
                URI uri = URI.create(url);
                String userInfo = uri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    String[] userParts = userInfo.split(":", 2);
                    if (username == null || username.isBlank() || username.startsWith("${")) {
                        username = userParts[0];
                    }
                    if (password == null || password.isBlank() || password.startsWith("${")) {
                        password = userParts[1];
                    }
                }
                String host = uri.getHost();
                int port = uri.getPort() > 0 ? uri.getPort() : 3306;
                String path = uri.getPath();
                String query = uri.getQuery();

                url = "jdbc:mysql://" + host + ":" + port + path + (query != null && !query.isBlank() ? "?" + query : "?sslMode=REQUIRED");
            } catch (Exception e) {
                url = "jdbc:" + url;
            }
        }

        if (!url.startsWith("jdbc:mysql://")) {
            url = "jdbc:mysql://" + url.replaceFirst("^jdbc:", "");
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username != null && !username.startsWith("${") ? username.trim() : "");
        hikariConfig.setPassword(password != null && !password.startsWith("${") ? password.trim() : "");
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setIdleTimeout(300000);
        hikariConfig.setConnectionTimeout(20000);
        hikariConfig.setPoolName("CityBrainHikariPool");

        log.info("Initialized CityBrain DataSource with target: {}", sanitizeUrl(url));
        return new HikariDataSource(hikariConfig);
    }

    private String sanitizeUrl(String url) {
        return url.replaceAll("(?i)(password=)[^&;]*", "$1***");
    }
}