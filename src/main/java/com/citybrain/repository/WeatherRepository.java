package com.citybrain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.citybrain.model.Weather;

public interface WeatherRepository extends JpaRepository<Weather, Long> {
}