package com.citybrain.service;

import com.citybrain.dto.LivePollutionDto;
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
public class PollutionApiService {

    private static final Logger log = LoggerFactory.getLogger(PollutionApiService.class);
    private static final long CACHE_DURATION_MS = 5 * 60 * 1000; // 5 minutes cache

    public static final double DEFAULT_LAT = 17.3850;
    public static final double DEFAULT_LON = 78.4867;
    public static final String DEFAULT_CITY = "Greater Hyderabad";

    private final HttpClient httpClient;
    private final ConcurrentHashMap<String, CachedPollution> cache = new ConcurrentHashMap<>();

    private record CachedPollution(LivePollutionDto data, long timestampMs) {}

    public PollutionApiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(6))
                .build();
    }

    /**
     * Retrieves current live Air Quality for coordinates with 5-minute caching.
     */
    public LivePollutionDto getLivePollution(Double lat, Double lon, boolean forceRefresh) {
        final double latitude = (lat != null) ? lat : DEFAULT_LAT;
        final double longitude = (lon != null) ? lon : DEFAULT_LON;
        final String cacheKey = String.format(Locale.ROOT, "%.4f,%.4f", latitude, longitude);

        long now = System.currentTimeMillis();
        CachedPollution cached = cache.get(cacheKey);
        if (!forceRefresh && cached != null && (now - cached.timestampMs()) < CACHE_DURATION_MS) {
            log.debug("Returning cached live air quality for key: {}", cacheKey);
            return cached.data();
        }

        try {
            LivePollutionDto freshData = fetchFromOpenMeteoAirQuality(latitude, longitude);
            cache.put(cacheKey, new CachedPollution(freshData, now));
            return freshData;
        } catch (Exception e) {
            log.error("Failed to fetch live air quality: {}", e.getMessage());
            if (cached != null) {
                log.warn("Serving stale cached air quality due to fetch failure.");
                return cached.data();
            }
            throw new RuntimeException("Live air quality service temporarily unavailable: " + e.getMessage(), e);
        }
    }

    private LivePollutionDto fetchFromOpenMeteoAirQuality(double lat, double lon) throws Exception {
        String url = String.format(
                Locale.ROOT,
                "https://air-quality-api.open-meteo.com/v1/air-quality?latitude=%.4f&longitude=%.4f&current=european_aqi,us_aqi,pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone&timezone=auto",
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
            throw new RuntimeException("Open-Meteo Air Quality returned HTTP " + response.statusCode());
        }

        String json = response.body();

        LivePollutionDto dto = new LivePollutionDto();
        dto.setLocation(DEFAULT_CITY);
        dto.setLatitude(lat);
        dto.setLongitude(lon);
        dto.setFetchedAt(LocalDateTime.now());
        dto.setLive(true);
        dto.setSource("Open-Meteo Air Quality API");

        int currentIdx = json.indexOf("\"current\"");
        String currentSlice = (currentIdx != -1) ? json.substring(currentIdx) : json;

        Double usAqi = WeatherApiService.extractDouble(currentSlice, "us_aqi");
        int aqiVal = (usAqi != null) ? usAqi.intValue() : 85;
        dto.setAqi(aqiVal);

        Double euAqi = WeatherApiService.extractDouble(currentSlice, "european_aqi");
        dto.setEuropeanAqi(euAqi != null ? euAqi.intValue() : 45);

        Double pm25 = WeatherApiService.extractDouble(currentSlice, "pm2_5");
        dto.setPm25(pm25 != null ? pm25 : 28.5);

        Double pm10 = WeatherApiService.extractDouble(currentSlice, "pm10");
        dto.setPm10(pm10 != null ? pm10 : 45.0);

        Double co = WeatherApiService.extractDouble(currentSlice, "carbon_monoxide");
        dto.setCarbonMonoxide(co != null ? co : 400.0);

        Double no2 = WeatherApiService.extractDouble(currentSlice, "nitrogen_dioxide");
        dto.setNitrogenDioxide(no2 != null ? no2 : 15.0);

        Double so2 = WeatherApiService.extractDouble(currentSlice, "sulphur_dioxide");
        dto.setSulphurDioxide(so2 != null ? so2 : 8.0);

        Double o3 = WeatherApiService.extractDouble(currentSlice, "ozone");
        dto.setOzone(o3 != null ? o3 : 65.0);

        dto.setCategory(mapAqiCategory(aqiVal));
        dto.setDominantPollutant((pm25 != null && pm25 > 35.0) ? "PM2.5" : "PM10");

        String time = WeatherApiService.extractString(currentSlice, "time");
        dto.setObservationTime(time != null ? time : LocalDateTime.now().toString());

        return dto;
    }

    public static String mapAqiCategory(int aqi) {
        if (aqi <= 50) return "Good";
        if (aqi <= 100) return "Moderate";
        if (aqi <= 150) return "Unhealthy for Sensitive Groups";
        if (aqi <= 200) return "Unhealthy";
        if (aqi <= 300) return "Very Unhealthy";
        return "Hazardous";
    }
}
