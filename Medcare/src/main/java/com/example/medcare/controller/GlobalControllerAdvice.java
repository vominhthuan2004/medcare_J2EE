package com.example.medcare.controller;

import com.example.medcare.model.User;
import com.example.medcare.repository.CartItemRepository;
import com.example.medcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final CartItemRepository cartRepo;
    private final UserRepository userRepo;

    @ModelAttribute("cartCount")
    public int cartCount(Principal principal) {

        if (principal == null) return 0;

        User user = userRepo.findByUsername(principal.getName())
                .orElse(null);

        if (user == null) return 0;

        return cartRepo.findByUser(user)
                .stream()
                .mapToInt(c -> c.getQuantity())
                .sum();
    }
}