package com.citybrain.controller;

import com.citybrain.dto.LivePollutionDto;
import com.citybrain.dto.LiveTrafficDto;
import com.citybrain.dto.LiveWeatherDto;
import com.citybrain.service.PollutionApiService;
import com.citybrain.service.TrafficApiService;
import com.citybrain.service.WeatherApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/live")
public class LiveTelemetryController {

    private final WeatherApiService weatherApiService;
    private final PollutionApiService pollutionApiService;
    private final TrafficApiService trafficApiService;

    public LiveTelemetryController(
            WeatherApiService weatherApiService,
            PollutionApiService pollutionApiService,
            TrafficApiService trafficApiService
    ) {
        this.weatherApiService = weatherApiService;
        this.pollutionApiService = pollutionApiService;
        this.trafficApiService = trafficApiService;
    }

    @GetMapping("/weather")
    public ResponseEntity<LiveWeatherDto> getLiveWeather(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false, defaultValue = "false") boolean refresh
    ) {
        LiveWeatherDto weather = weatherApiService.getLiveWeather(lat, lon, refresh);
        return ResponseEntity.ok(weather);
    }

    @GetMapping("/pollution")
    public ResponseEntity<LivePollutionDto> getLivePollution(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false, defaultValue = "false") boolean refresh
    ) {
        LivePollutionDto pollution = pollutionApiService.getLivePollution(lat, lon, refresh);
        return ResponseEntity.ok(pollution);
    }

    @GetMapping("/traffic")
    public ResponseEntity<LiveTrafficDto> getLiveTraffic(
            @RequestParam(required = false, defaultValue = "false") boolean refresh
    ) {
        LiveTrafficDto traffic = trafficApiService.getLiveTraffic(refresh);
        return ResponseEntity.ok(traffic);
    }
}
