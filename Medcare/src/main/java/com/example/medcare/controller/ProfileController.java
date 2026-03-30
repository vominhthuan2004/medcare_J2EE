package com.example.medcare.controller;

import com.example.medcare.model.User;
import com.example.medcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;

    @GetMapping
    public String showProfile(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String phone,
                                @RequestParam String address,
                                Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setAddress(address);
        
        userRepository.save(user);
        
        return "redirect:/profile?success";
    }
}
