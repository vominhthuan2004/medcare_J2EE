package com.example.medcare.controller;

import com.example.medcare.model.Order;
import com.example.medcare.model.User;
import com.example.medcare.repository.OrderRepository;
import com.example.medcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/my-orders")
@RequiredArgsConstructor
public class UserOrderController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @GetMapping
    public String viewMyOrders(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        model.addAttribute("orders", orders);

        return "my-orders";
    }

    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable Long id, Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Bảo mật: Chỉ cho phép xem đơn hàng của chính mình (nếu không phải Admin)
        if (!order.getUser().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/my-orders";
        }

        model.addAttribute("order", order);
        model.addAttribute("items", order.getOrderItems());

        return "my-order-detail";
    }
}
