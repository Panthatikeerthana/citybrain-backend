package com.citybrain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.citybrain.model.Traffic;

public interface TrafficRepository extends JpaRepository<Traffic, Long> {

}