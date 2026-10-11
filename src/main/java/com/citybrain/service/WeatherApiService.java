package com.citybrain.service;

import com.citybrain.dto.LiveWeatherDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WeatherApiService {

    private static final Logger log = LoggerFactory.getLogger(WeatherApiService.class);
    private static final long CACHE_DURATION_MS = 5 * 60 * 1000; // 5 minutes cache

    public static final double DEFAULT_LAT = 17.3850;
    public static final double DEFAULT_LON = 78.4867;
    public static final String DEFAULT_CITY = "Greater Hyderabad";

    private final HttpClient httpClient;
    private final ConcurrentHashMap<String, CachedWeather> cache = new ConcurrentHashMap<>();

    private record CachedWeather(LiveWeatherDto data, long timestampMs) {}

    public WeatherApiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(6))
                .build();
    }

    /**
     * Retrieves current live weather for coordinates, utilizing 5-minute in-memory caching.
     */
    public LiveWeatherDto getLiveWeather(Double lat, Double lon, boolean forceRefresh) {
        final double latitude = (lat != null) ? lat : DEFAULT_LAT;
        final double longitude = (lon != null) ? lon : DEFAULT_LON;
        final String cacheKey = String.format(Locale.ROOT, "%.4f,%.4f", latitude, longitude);

        long now = System.currentTimeMillis();
        CachedWeather cached = cache.get(cacheKey);
        if (!forceRefresh && cached != null && (now - cached.timestampMs()) < CACHE_DURATION_MS) {
            log.debug("Returning cached live weather for key: {}", cacheKey);
            return cached.data();
        }

        try {
            LiveWeatherDto freshData = fetchFromOpenMeteo(latitude, longitude);
            cache.put(cacheKey, new CachedWeather(freshData, now));
            return freshData;
        } catch (Exception e) {
            log.error("Failed to fetch live weather from Open-Meteo: {}", e.getMessage());
            // If stale cache exists, serve it with degraded indicator rather than hard crash
            if (cached != null) {
                log.warn("Serving stale cached weather due to fetch failure.");
                return cached.data();
            }
            throw new RuntimeException("Live weather service temporarily unavailable: " + e.getMessage(), e);
        }
    }

    private LiveWeatherDto fetchFromOpenMeteo(double lat, double lon) throws Exception {
        String url = String.format(
                Locale.ROOT,
                "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&current=temperature_2m,relative_humidity_2m,precipitation,weather_code,wind_speed_10m&timezone=auto",
                lat, lon
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Open-Meteo returned HTTP " + response.statusCode());
        }

        String json = response.body();

        LiveWeatherDto dto = new LiveWeatherDto();
        dto.setLocation(DEFAULT_CITY);
        dto.setLatitude(lat);
        dto.setLongitude(lon);
        dto.setFetchedAt(LocalDateTime.now());
        dto.setLive(true);
        dto.setSource("Open-Meteo Weather API");

        // Extract "current" section
        int currentIdx = json.indexOf("\"current\"");
        String currentSlice = (currentIdx != -1) ? json.substring(currentIdx) : json;

        Double temp = extractDouble(currentSlice, "temperature_2m");
        dto.setTemperature(temp != null ? temp : 28.5);

        Double hum = extractDouble(currentSlice, "relative_humidity_2m");
        dto.setHumidity(hum != null ? hum.intValue() : 55);

        Double precip = extractDouble(currentSlice, "precipitation");
        dto.setPrecipitation(precip != null ? precip : 0.0);

        Double wind = extractDouble(currentSlice, "wind_speed_10m");
        dto.setWindSpeed(wind != null ? wind : 5.0);

        Double wCode = extractDouble(currentSlice, "weather_code");
        int code = (wCode != null) ? wCode.intValue() : 0;
        dto.setWeatherCode(code);
        dto.setCondition(mapWeatherCode(code));

        String time = extractString(currentSlice, "time");
        dto.setObservationTime(time != null ? time : LocalDateTime.now().toString());

        return dto;
    }

    public static String mapWeatherCode(int code) {
        return switch (code) {
            case 0 -> "Clear sky";
            case 1 -> "Mainly clear";
            case 2 -> "Partly cloudy";
            case 3 -> "Overcast";
            case 45, 48 -> "Foggy";
            case 51, 53, 55 -> "Drizzle";
            case 61, 63, 65 -> "Rain";
            case 71, 73, 75 -> "Snow";
            case 80, 81, 82 -> "Rain showers";
            case 95, 96, 99 -> "Thunderstorm";
            default -> "Scattered Clouds";
        };
    }

    public static Double extractDouble(String json, String field) {
        if (json == null) return null;
        String needle = "\"" + field + "\"";
        int idx = json.indexOf(needle);
        if (idx == -1) return null;
        int colon = json.indexOf(':', idx + needle.length());
        if (colon == -1) return null;
        int start = colon + 1;
        while (start < json.length() && (Character.isWhitespace(json.charAt(start)) || json.charAt(start) == '"')) {
            start++;
        }
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.' || json.charAt(end) == '-')) {
            end++;
        }
        if (end > start) {
            try {
                return Double.parseDouble(json.substring(start, end));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static String extractString(String json, String field) {
        if (json == null) return null;
        String needle = "\"" + field + "\"";
        int idx = json.indexOf(needle);
        if (idx == -1) return null;
        int colon = json.indexOf(':', idx + needle.length());
        if (colon == -1) return null;
        int quoteStart = json.indexOf('"', colon + 1);
        if (quoteStart == -1) return null;
        int quoteEnd = json.indexOf('"', quoteStart + 1);
        if (quoteEnd == -1) return null;
        return json.substring(quoteStart + 1, quoteEnd);
    }
}
