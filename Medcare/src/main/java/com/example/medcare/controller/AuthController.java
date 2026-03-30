package com.example.medcare.controller;

import com.example.medcare.model.User;
import com.example.medcare.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                           @RequestParam("confirmPassword") String confirmPassword,
                           Model model) {
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu nhập lại không khớp!");
            return "register";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        repo.save(user);
        return "redirect:/login";
    }
}