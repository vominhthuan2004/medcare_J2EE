package com.example.medcare.controller;

import com.example.medcare.model.HealthRecord;
import com.example.medcare.model.User;
import com.example.medcare.repository.HealthRecordRepository;
import com.example.medcare.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class HealthController {

    private final HealthRecordRepository repo;
    private final UserRepository userRepo;

    public HealthController(HealthRecordRepository repo,
                            UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }



    // ================= SAVE =================
    @PostMapping("/save")
    public String save(@ModelAttribute HealthRecord record,
                       Principal principal) {

        double bmi = record.getWeight() /
                Math.pow(record.getHeight() / 100, 2);

        record.setBmi(bmi);
        record.setDate(LocalDate.now());

        record.setStatus(getBmiStatus(bmi));

        User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        record.setUser(user);

        repo.save(record);

        return "redirect:/dashboard";
    }

    // ================= EDIT =================
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       Model model,
                       Principal principal) {

        HealthRecord record =
                repo.findByIdAndUserUsername(id, principal.getName());

        model.addAttribute("record", record);
        return "edit";
    }

    // ================= UPDATE =================
    @PostMapping("/update")
    public String update(@ModelAttribute HealthRecord record,
                         Principal principal) {

        double bmi = record.getWeight() /
                Math.pow(record.getHeight() / 100, 2);

        record.setBmi(bmi);
        record.setStatus(getBmiStatus(bmi));

        User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        record.setUser(user);

        repo.save(record);

        return "redirect:/dashboard";
    }

    // ================= DELETE =================
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         Principal principal) {

        HealthRecord record =
                repo.findByIdAndUserUsername(id, principal.getName());

        repo.delete(record);

        return "redirect:/dashboard";
    }

    // ================= WHO BMI CLASSIFICATION =================
    private String getBmiStatus(double bmi) {

        if (bmi < 16) {
            return "Gầy độ III";
        } else if (bmi < 17) {
            return "Gầy độ II";
        } else if (bmi < 18.5) {
            return "Gầy độ I";
        } else if (bmi < 25) {
            return "Bình thường";
        } else if (bmi < 30) {
            return "Thừa cân";
        } else if (bmi < 35) {
            return "Béo phì độ I";
        } else if (bmi < 40) {
            return "Béo phì độ II";
        } else {
            return "Béo phì độ III";
        }
    }
}