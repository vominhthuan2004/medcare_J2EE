package com.example.medcare.controller;

import com.example.medcare.model.HealthRecord;
import com.example.medcare.model.User;
import com.example.medcare.repository.HealthRecordRepository;
import com.example.medcare.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private HealthRecordRepository recordRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {

        String username = principal.getName();
        List<HealthRecord> records = recordRepository.findByUserUsername(username);

        model.addAttribute("records", records);

        if (!records.isEmpty()) {
            HealthRecord latest = records.get(records.size() - 1);

            model.addAttribute("latestBMI", latest.getBmi());
            model.addAttribute("latestWeight", latest.getWeight());
            model.addAttribute("latestStatus", latest.getStatus());
            model.addAttribute("totalRecords", records.size());
        } else {
            model.addAttribute("latestBMI", 0);
            model.addAttribute("latestWeight", 0);
            model.addAttribute("latestStatus", "--");
            model.addAttribute("totalRecords", 0);
        }
        String advice = "Dữ liệu chưa đủ để phân tích.";

        if (records.size() >= 2) {

            HealthRecord last = records.get(records.size() - 1);
            HealthRecord prev = records.get(records.size() - 2);

            if (last.getWeight() > prev.getWeight()) {
                advice = "⚠ Bạn đang tăng cân. Hãy kiểm soát chế độ ăn.";
            }

            if (last.getBmi() >= 25) {
                advice = "⚠ BMI đang ở mức thừa cân. Nên tập luyện nhiều hơn.";
            }

            if (last.getBmi() >= 30) {
                advice = "🚨 BMI ở mức béo phì. Cần điều chỉnh gấp.";
            }

            if (last.getBmi() >= 18.5 && last.getBmi() < 25) {
                advice = "✅ Chỉ số sức khỏe ổn định. Tiếp tục duy trì!";
            }
        }

        model.addAttribute("aiAdvice", advice);
        String dietAdvice = "Chưa có dữ liệu BMI.";

        if (!records.isEmpty()) {

            HealthRecord last = records.get(records.size() - 1);
            double bmi = last.getBmi();

            if (bmi < 18.5) {
                dietAdvice = """
                🥑 Bạn đang thiếu cân.
                - Tăng khẩu phần protein (trứng, ức gà, cá hồi)
                - Uống sữa hoặc whey
                - Ăn thêm tinh bột tốt (gạo lứt, khoai lang)
                """;
            }
            else if (bmi < 25) {
                dietAdvice = """
                🥗 BMI bình thường.
                - Duy trì chế độ ăn cân bằng
                - 50% rau xanh
                - 25% protein
                - 25% tinh bột
                """;
            }
            else if (bmi < 30) {
                dietAdvice = """
                🍎 Bạn đang thừa cân.
                - Giảm đồ ngọt và chiên rán
                - Tăng rau xanh và protein nạc
                - Hạn chế tinh bột buổi tối
                """;
            }
            else {
                dietAdvice = """
                🚨 BMI béo phì.
                - Cắt giảm đường hoàn toàn
                - Ăn nhiều rau, cá, ức gà
                - Tập cardio 30 phút/ngày
                """;
            }
        }

        model.addAttribute("dietAdvice", dietAdvice);
        return "dashboard";
    }
}