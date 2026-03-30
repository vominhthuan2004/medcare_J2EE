package com.example.medcare.controller;

import com.example.medcare.model.Product;
import com.example.medcare.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductRepository productRepository;

    // 📋 Danh sách
    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "admin/products";   // khớp với products.html
    }

    // ➕ Form tạo mới
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/product-form";
    }

    // 💾 Save (create + update)
    @PostMapping("/save")
    public String save(@ModelAttribute Product product,
                       @RequestParam("imageFile") MultipartFile file) throws IOException {

        if (!file.isEmpty()) {

            // Lấy đường dẫn thư mục static
            String uploadDir = new File("src/main/resources/static/images/products")
                    .getAbsolutePath();

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            File saveFile = new File(uploadDir, fileName);
            file.transferTo(saveFile);

            product.setImageUrl("/images/products/" + fileName);

        } else {
            // Nếu không upload ảnh mới → giữ ảnh cũ khi edit
            if (product.getId() != null) {
                Product existing = productRepository.findById(product.getId()).orElse(null);
                if (existing != null) {
                    product.setImageUrl(existing.getImageUrl());
                }
            }
        }

        productRepository.save(product);
        return "redirect:/admin/products";
    }

    // ✏️ Sửa
    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product id: " + id));

        model.addAttribute("product", product);
        return "admin/product-form"; // đúng tên file html
    }

    // 🗑 Xóa
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/admin/products";
    }
}