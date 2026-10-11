package com.citybrain.service;

import com.citybrain.dto.LiveTrafficDto;
import com.citybrain.dto.TrafficFlowItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class TrafficApiService {

    private static final Logger log = LoggerFactory.getLogger(TrafficApiService.class);
    private static final long CACHE_DURATION_MS = 2 * 60 * 1000; // 2 minutes cache for live traffic

    @Value("${traffic.api.key:}")
    private String configuredApiKey;

    private final HttpClient httpClient;
    private LiveTrafficDto cachedTraffic;
    private long cacheTimestampMs = 0;

    private static record TrafficNode(String name, double lat, double lon) {}

    // Key Hyderabad arterial traffic junctions
    private static final List<TrafficNode> KEY_CORRIDORS = List.of(
            new TrafficNode("Uppal Junction", 17.3984, 78.5583),
            new TrafficNode("Hitec City Mindspace", 17.4435, 78.3772),
            new TrafficNode("LB Nagar Ring Road", 17.3457, 78.5522),
            new TrafficNode("Kukatpally Y Junction", 17.4938, 78.3989),
            new TrafficNode("Secunderabad Station", 17.4399, 78.4983)
    );

    public TrafficApiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(6))
                .build();
    }

    /**
     * Resolves the TomTom API key in order of priority:
     * 1. Spring application property (traffic.api.key)
     * 2. OS environment variable (TRAFFIC_API_KEY)
     * 3. JVM System property (traffic.api.key or TRAFFIC_API_KEY)
     * 4. Server-side local .env file (gitignored, never exposed)
     */
    public String getApiKey() {
        if (configuredApiKey != null && !configuredApiKey.isBlank() && !configuredApiKey.contains("${")) {
            return configuredApiKey.trim();
        }
        String envKey = System.getenv("TRAFFIC_API_KEY");
        if (envKey != null && !envKey.isBlank()) {
            return envKey.trim();
        }
        String propKey = System.getProperty("traffic.api.key");
        if (propKey != null && !propKey.isBlank()) {
            return propKey.trim();
        }
        String propKeyEnv = System.getProperty("TRAFFIC_API_KEY");
        if (propKeyEnv != null && !propKeyEnv.isBlank()) {
            return propKeyEnv.trim();
        }
        String dotEnvKey = readKeyFromDotEnv();
        if (dotEnvKey != null && !dotEnvKey.isBlank()) {
            return dotEnvKey.trim();
        }
        return null;
    }

    private String readKeyFromDotEnv() {
        String[] potentialPaths = {
                ".env",
                "../.env",
                "c:\\Users\\panth\\OneDrive\\Desktop\\citybrainn\\.env"
        };
        for (String path : potentialPaths) {
            try {
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            line = line.trim();
                            if (line.startsWith("TRAFFIC_API_KEY=") || line.startsWith("traffic.api.key=")) {
                                int eqIdx = line.indexOf('=');
                                if (eqIdx != -1 && eqIdx < line.length() - 1) {
                                    String val = line.substring(eqIdx + 1).trim();
                                    if ((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) {
                                        val = val.substring(1, val.length() - 1).trim();
                                    }
                                    if (!val.isBlank() && !val.equalsIgnoreCase("your_tomtom_api_key_here")) {
                                        return val;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public boolean isConfigured() {
        String key = getApiKey();
        return key != null && !key.isBlank() && !key.equalsIgnoreCase("your_tomtom_api_key_here");
    }

    /**
     * Retrieves live traffic telemetry.
     * If TRAFFIC_API_KEY is missing, returns CONFIG_REQUIRED response.
     * Caches successful responses for 2 minutes to respect rate limits.
     */
    public LiveTrafficDto getLiveTraffic(boolean forceRefresh) {
        String apiKey = getApiKey();
        if (apiKey == null || apiKey.isBlank() || apiKey.equalsIgnoreCase("your_tomtom_api_key_here")) {
            return LiveTrafficDto.configRequired();
        }

        long now = System.currentTimeMillis();
        if (!forceRefresh && cachedTraffic != null && (now - cacheTimestampMs) < CACHE_DURATION_MS) {
            log.debug("Returning cached live traffic telemetry (age: {}s)", (now - cacheTimestampMs) / 1000);
            return cachedTraffic;
        }

        try {
            LiveTrafficDto fresh = fetchFromTomTom(apiKey);
            if (fresh.isLive()) {
                cachedTraffic = fresh;
                cacheTimestampMs = now;
            }
            return fresh;
        } catch (Exception e) {
            log.error("Failed to fetch live traffic: {}", e.getMessage());
            if (cachedTraffic != null) {
                return cachedTraffic;
            }
            LiveTrafficDto errorDto = new LiveTrafficDto();
            errorDto.setConfigured(true);
            errorDto.setLive(false);
            errorDto.setStatus("ERROR");
            errorDto.setMessage("Live traffic service temporarily unavailable: " + e.getMessage());
            return errorDto;
        }
    }

    /**
     * Fetches real-time traffic flow segment data from TomTom Traffic Flow API.
     * Endpoint: https://api.tomtom.com/traffic/services/4/flowSegmentData/relative0/10/json
     * Never exposes the API key in logs, exceptions, or responses.
     */
    private LiveTrafficDto fetchFromTomTom(String apiKey) {
        LiveTrafficDto dto = new LiveTrafficDto();
        dto.setConfigured(true);
        dto.setProvider("TomTom Traffic Flow API");

        List<TrafficFlowItem> items = new ArrayList<>();
        String timeStr = LocalDateTime.now().toString();

        int authFailureCount = 0;
        int rateLimitCount = 0;
        int timeoutCount = 0;

        for (TrafficNode node : KEY_CORRIDORS) {
            try {
                String url = String.format(
                        Locale.ROOT,
                        "https://api.tomtom.com/traffic/services/4/flowSegmentData/relative0/10/json?point=%.4f,%.4f&key=%s",
                        node.lat(), node.lon(), apiKey
                );

                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(6))
                        .GET()
                        .build();

                HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

                if (resp.statusCode() == 200) {
                    String body = resp.body();
                    Double curSpd = WeatherApiService.extractDouble(body, "currentSpeed");
                    Double freeSpd = WeatherApiService.extractDouble(body, "freeFlowSpeed");
                    Double conf = WeatherApiService.extractDouble(body, "confidence");

                    int cur = (curSpd != null) ? curSpd.intValue() : 25;
                    int free = (freeSpd != null) ? freeSpd.intValue() : 50;
                    double confidence = (conf != null) ? conf : 0.95;

                    boolean roadClosure = body.contains("\"roadClosure\":true") || body.contains("\"roadClosure\": true");

                    int congestion = 0;
                    if (roadClosure) {
                        congestion = 100;
                    } else if (free > 0) {
                        congestion = (int) Math.max(0, Math.min(100, Math.round((1.0 - ((double) cur / free)) * 100)));
                    }

                    String status = roadClosure ? "Road Closed" : (congestion > 70) ? "Heavy" : (congestion > 40) ? "Moderate" : "Smooth";

                    items.add(new TrafficFlowItem(
                            node.name(), node.lat(), node.lon(),
                            congestion, cur, free, status, confidence, timeStr
                    ));
                } else if (resp.statusCode() == 401 || resp.statusCode() == 403) {
                    authFailureCount++;
                    log.error("TomTom authorization failed for node {} with HTTP {}", node.name(), resp.statusCode());
                } else if (resp.statusCode() == 429) {
                    rateLimitCount++;
                    log.warn("TomTom rate limit reached for node {} (HTTP 429)", node.name());
                } else {
                    log.warn("TomTom upstream responded with HTTP {} for node {}", resp.statusCode(), node.name());
                }
            } catch (HttpTimeoutException te) {
                timeoutCount++;
                log.warn("TomTom request timed out for node {}", node.name());
            } catch (Exception e) {
                log.warn("TomTom request error for node {}: {}", node.name(), e.getMessage());
            }
        }

        if (items.isEmpty()) {
            dto.setLive(false);
            if (authFailureCount > 0) {
                dto.setStatus("AUTH_ERROR");
                dto.setMessage("Invalid TomTom API key or authorization rejected by TomTom.");
            } else if (rateLimitCount > 0) {
                dto.setStatus("RATE_LIMITED");
                dto.setMessage("TomTom Traffic API rate limit exceeded. Please try again shortly.");
            } else if (timeoutCount > 0) {
                dto.setStatus("TIMEOUT");
                dto.setMessage("TomTom Traffic API connection timed out.");
            } else {
                dto.setStatus("ERROR");
                dto.setMessage("Unable to retrieve real-time flow data from TomTom Traffic API.");
            }
            return dto;
        }

        dto.setLive(true);
        dto.setStatus("ACTIVE");
        dto.setMessage("Live telemetry successfully retrieved from TomTom Traffic Flow API.");
        dto.setItems(items);
        return dto;
    }
}
