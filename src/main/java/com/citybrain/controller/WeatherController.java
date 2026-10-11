package com.citybrain.controller;

import com.citybrain.exception.ResourceNotFoundException;
import com.citybrain.model.Weather;
import com.citybrain.repository.WeatherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherRepository weatherRepository;

    public WeatherController(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @GetMapping
    public List<Weather> getAllWeather() {
        return weatherRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Weather> getWeatherById(@PathVariable Long id) {
        Weather weather = weatherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Weather", "id", id));
        return ResponseEntity.ok(weather);
    }

    @PostMapping
    public ResponseEntity<Weather> createWeather(@RequestBody Weather weather) {
        weather.setId(null);
        if (weather.getRecordedAt() == null) {
            weather.setRecordedAt(LocalDateTime.now());
        }
        Weather saved = weatherRepository.save(weather);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Weather> updateWeather(@PathVariable Long id, @RequestBody Weather updated) {
        Weather existing = weatherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Weather", "id", id));

        if (updated.getLocation() != null) existing.setLocation(updated.getLocation());
        if (updated.getTemperature() != null) existing.setTemperature(updated.getTemperature());
        if (updated.getHumidity() != null) existing.setHumidity(updated.getHumidity());
        if (updated.getRainfall() != null) existing.setRainfall(updated.getRainfall());

        return ResponseEntity.ok(weatherRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeather(@PathVariable Long id) {
        Weather existing = weatherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Weather", "id", id));
        weatherRepository.delete(existing);
        return ResponseEntity.noContent().build();
    }
}
