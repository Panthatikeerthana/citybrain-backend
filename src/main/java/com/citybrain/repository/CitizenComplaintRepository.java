package com.citybrain.repository;

import com.citybrain.model.CitizenComplaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitizenComplaintRepository extends JpaRepository<CitizenComplaint, Long> {
    List<CitizenComplaint> findByStatus(String status);
    List<CitizenComplaint> findByCategory(String category);
    List<CitizenComplaint> findByLocationContainingIgnoreCase(String location);
}
