package com.example.medcare.repository;

import com.example.medcare.model.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    List<HealthRecord> findByUserUsername(String username);

    HealthRecord findByIdAndUserUsername(Long id, String username);
}