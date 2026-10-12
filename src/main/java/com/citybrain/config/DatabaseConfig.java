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
        String url = configuredUrl != null ? configuredUrl.trim() : "";

        // Resolve from environment if placeholder was not resolved
        if (url.isEmpty() || url.startsWith("${") || url.equalsIgnoreCase("${DB_URL}")) {
            String envUrl = System.getenv("DB_URL");
            if (envUrl != null && !envUrl.isBlank()) {
                url = envUrl.trim();
            }
        }

        // Validate that a real JDBC URL was provided
        if (url.isEmpty() || url.startsWith("${")) {
            throw new IllegalStateException(
                "Production database URL is missing or set to an unresolved placeholder: '" + url + "'. " +
                "Please configure a valid MySQL JDBC URL (e.g. jdbc:mysql://<host>:<port>/<dbname>?useSSL=true) " +
                "in your Render Dashboard Environment Variables under DB_URL."
            );
        }

        // Ensure jdbc: prefix for cloud MySQL URLs (e.g. mysql:// -> jdbc:mysql://)
        if (url.startsWith("mysql://")) {
            url = "jdbc:" + url;
        }

        String username = configuredUsername;
        if (username == null || username.isBlank() || username.startsWith("${")) {
            username = System.getenv("DB_USERNAME");
        }

        String password = configuredPassword;
        if (password == null || password.isBlank() || password.startsWith("${")) {
            password = System.getenv("DB_PASSWORD");
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username != null ? username.trim() : "");
        hikariConfig.setPassword(password != null ? password.trim() : "");
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