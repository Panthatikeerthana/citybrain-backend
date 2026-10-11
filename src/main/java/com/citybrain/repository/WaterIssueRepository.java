package com.citybrain.repository;

import com.citybrain.model.WaterIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaterIssueRepository extends JpaRepository<WaterIssue, Long> {
    List<WaterIssue> findByStatus(String status);
}
