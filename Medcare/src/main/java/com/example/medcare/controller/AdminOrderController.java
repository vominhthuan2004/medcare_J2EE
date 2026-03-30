package com.example.medcare.controller;


import com.example.medcare.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import com.example.medcare.model.Order;
import com.example.medcare.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final OrderRepository orderRepository;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {

        List<Order> orders = orderRepo.findAll();

        double totalRevenue = orders.stream()
                .mapToDouble(Order::getTotal)
                .sum();

        long totalOrders = orders.size();
        long totalUsers = userRepo.count();

        // Doanh thu hôm nay
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        double todayRevenue = orders.stream()
                .filter(o -> o.getCreatedAt().isAfter(todayStart))
                .mapToDouble(Order::getTotal)
                .sum();

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("todayRevenue", todayRevenue);
        model.addAttribute("orders", orders);

        return "admin/dashboard";
    }
    @GetMapping("/admin/order/{id}")
    public String viewOrderDetail(@PathVariable Long id, Model model) {

        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        model.addAttribute("order", order);
        model.addAttribute("items", order.getOrderItems());

        return "admin/order-detail";
    }

    @PostMapping("/admin/order/update-status/{id}")
    @Transactional
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status) {

        Order order = orderRepository.findById(id)
                .orElseThrow();

        order.setStatus(Order.OrderStatus.valueOf(status));

        return "redirect:/admin/order/" + id;
    }

    @PostMapping("/admin/order/delete/{id}")
    @Transactional
    public String deleteOrder(@PathVariable Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Items should be cascade deleted if CascadeType is properly set on the entity, 
        // but explicit removal is safer.
        orderRepo.delete(order);
        
        return "redirect:/admin/dashboard";
    }
}