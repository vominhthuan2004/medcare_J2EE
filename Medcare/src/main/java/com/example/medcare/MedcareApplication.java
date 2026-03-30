package com.example.medcare;

import com.example.medcare.model.Product;
import com.example.medcare.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MedcareApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedcareApplication.class, args);
    }

    @Bean
    CommandLineRunner initProducts(ProductRepository repo) {
        return args -> {

            if (repo.count() == 0) {

                repo.save(Product.builder()
                        .name("Omega 3")
                        .description("Hỗ trợ tim mạch và giảm cholesterol")
                        .price(250000)
                        .imageUrl("/images/products/omega3.jpg")
                        .stock(50)
                        .build());

                repo.save(Product.builder()
                        .name("Vitamin D3")
                        .description("Tăng cường miễn dịch và xương chắc khỏe")
                        .price(180000)
                        .imageUrl("/images/products/vitamin-d3.jpg")
                        .stock(40)
                        .build());

                repo.save(Product.builder()
                        .name("Whey Protein")
                        .description("Hỗ trợ tăng cơ và phục hồi sau tập luyện")
                        .price(850000)
                        .imageUrl("/images/products/whey.jpg")
                        .stock(30)
                        .build());
            }
        };
    }

    @Bean
    CommandLineRunner initUsers(com.example.medcare.repository.UserRepository userRepo, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepo.findByUsername("admin").isEmpty()) {
                com.example.medcare.model.User admin = new com.example.medcare.model.User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setRole("ROLE_ADMIN");
                admin.setFullName("Administrator");
                admin.setPhone("0123456789");
                admin.setAddress("Hệ thống MEDCARE");
                userRepo.save(admin);
                System.out.println("=====================================");
                System.out.println("✅ Đã tạo tài khoản ADMIN thành công!");
                System.out.println("Tài khoản: admin");
                System.out.println("Mật khẩu: 123456");
                System.out.println("=====================================");
            }
        };
    }
    @Bean
    CommandLineRunner fixImageUrls(ProductRepository repo) {
        return args -> {
            repo.findAll().forEach(p -> {
                String url = p.getImageUrl();
                if (url != null && !url.startsWith("/") && !url.startsWith("http")) {
                    p.setImageUrl("/images/products/" + url);
                    repo.save(p);
                    System.out.println("✅ Fixed imageUrl: " + url + " → " + p.getImageUrl());
                }
            });
        };
    }
    @Bean
    CommandLineRunner fixDatabaseEncoding(org.springframework.jdbc.core.JdbcTemplate jdbc, com.example.medcare.repository.HealthRecordRepository hrRepo) {
        return args -> {
            try {
                jdbc.execute("ALTER TABLE health_record ALTER COLUMN status NVARCHAR(255)");
                System.out.println("✅ Altered health_record.status to NVARCHAR(255)");
                
                hrRepo.findAll().forEach(r -> {
                    double bmi = r.getBmi();
                    String correctStatus;
                    if (bmi < 16) correctStatus = "Gầy độ III";
                    else if (bmi < 17) correctStatus = "Gầy độ II";
                    else if (bmi < 18.5) correctStatus = "Gầy độ I";
                    else if (bmi < 25) correctStatus = "Bình thường";
                    else if (bmi < 30) correctStatus = "Thừa cân";
                    else if (bmi < 35) correctStatus = "Béo phì độ I";
                    else if (bmi < 40) correctStatus = "Béo phì độ II";
                    else correctStatus = "Béo phì độ III";
                    
                    if (r.getStatus() == null || r.getStatus().contains("?")) {
                        r.setStatus(correctStatus);
                        hrRepo.save(r);
                        System.out.println("✅ Fixed corrupted status for record " + r.getId());
                    }
                });
            } catch (Exception e) {
                System.out.println("⚠️ Could not alter table. Error: " + e.getMessage());
            }
        };
    }
}