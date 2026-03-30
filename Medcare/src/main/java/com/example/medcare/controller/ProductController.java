package com.example.medcare.controller;

import com.example.medcare.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepo;

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productRepo.findAll());
        return "products";
    }
}