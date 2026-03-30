package com.example.medcare.controller;

import com.example.medcare.model.User;
import com.example.medcare.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 📋 Danh sách người dùng
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    // ✏️ Đổi role
    @PostMapping("/update-role/{id}")
    @Transactional
    public String updateRole(@PathVariable Long id,
                             @RequestParam String role,
                             RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Đã cập nhật quyền cho " + user.getUsername());
        return "redirect:/admin/users";
    }

    // 🔑 Reset mật khẩu (đặt lại thành 123456)
    @PostMapping("/reset-password/{id}")
    @Transactional
    public String resetPassword(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success",
                "Đã reset mật khẩu cho " + user.getUsername() + " thành 123456");
        return "redirect:/admin/users";
    }

    // 🗑 Xóa người dùng
    @PostMapping("/delete/{id}")
    @Transactional
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String username = user.getUsername();
        userRepository.delete(user);
        redirectAttributes.addFlashAttribute("success", "Đã xóa tài khoản " + username);
        return "redirect:/admin/users";
    }
}
