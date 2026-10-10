package com.citybrain.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.citybrain.model.Traffic;
import com.citybrain.repository.TrafficRepository;

import java.util.List;

@RestController
@RequestMapping("/api/traffic")
public class TrafficController {

    private final TrafficRepository trafficRepository;

    public TrafficController(TrafficRepository trafficRepository) {
        this.trafficRepository = trafficRepository;
    }

    @GetMapping
    public List<Traffic> getAllTraffic() {
        return trafficRepository.findAll();
    }
}