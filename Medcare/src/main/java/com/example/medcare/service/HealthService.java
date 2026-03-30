package com.example.medcare.service;

import com.example.medcare.model.HealthRecord;
import com.example.medcare.repository.HealthRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HealthService {

    private final HealthRecordRepository repo;

    public HealthService(HealthRecordRepository repo) {
        this.repo = repo;
    }

    public double calculateBMI(double weight, double height) {
        return weight / (height * height);
    }

    public String classifyBMI(double bmi) {
        if (bmi < 18.5) return "Thiếu cân";
        if (bmi < 25) return "Bình thường";
        if (bmi < 30) return "Thừa cân";
        return "Béo phì";
    }

    public void save(HealthRecord record) {
        double bmi = calculateBMI(record.getWeight(), record.getHeight());
        record.setBmi(bmi);
        record.setStatus(classifyBMI(bmi));
        record.setDate(LocalDate.now());
        repo.save(record);
    }

    public List<HealthRecord> getAll() {
        return repo.findAll();
    }
}