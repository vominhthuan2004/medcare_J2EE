package com.example.medcare.controller;

import com.example.medcare.model.*;
import com.example.medcare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final ProductRepository productRepo;
    private final CartItemRepository cartRepo;
    private final UserRepository userRepo;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;

    /* ===============================
       ADD TO CART
    =============================== */
    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, Principal principal) {

        User user = getCurrentUser(principal);
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem existing = cartRepo.findByUserAndProduct(user, product);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + 1);
            cartRepo.save(existing);
        } else {
            CartItem item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setQuantity(1);
            cartRepo.save(item);
        }

        return "redirect:/cart";
    }

    /* ===============================
       VIEW CART
    =============================== */
    @GetMapping
    public String viewCart(Model model, Principal principal) {

        User user = getCurrentUser(principal);
        List<CartItem> cartItems = cartRepo.findByUser(user);

        double total = cartItems.stream()
                .mapToDouble(item ->
                        item.getProduct().getPrice() * item.getQuantity())
                .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);

        return "cart";
    }

    /* ===============================
       REMOVE ITEM
    =============================== */
    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id, Principal principal) {

        User user = getCurrentUser(principal);
        cartRepo.deleteByUserIdAndProductId(user.getId(), id);

        return "redirect:/cart";
    }

    /* ===============================
   SHOW CHECKOUT PAGE
=============================== */

    @GetMapping("/checkout")
    public String showCheckout(Model model, Principal principal) {

        User user = getCurrentUser(principal);
        List<CartItem> cartItems = cartRepo.findByUser(user);

        double total = cartItems.stream()
                .mapToDouble(item ->
                        item.getProduct().getPrice() * item.getQuantity())
                .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);

        return "checkout";
    }
    @Transactional
    @PostMapping("/checkout")
    public String processCheckout(@RequestParam String fullName,
                                  @RequestParam String phone,
                                  @RequestParam String address,
                                  Principal principal) {

        User user = getCurrentUser(principal);
        List<CartItem> cartItems = cartRepo.findByUser(user);

        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        // ✅ Tạo Order trước
        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setReceiverName(fullName);
        order.setPhone(phone);
        order.setAddress(address);

        order = orderRepo.save(order); // 🔥 QUAN TRỌNG
        order.setStatus(Order.OrderStatus.PENDING);
        double total = 0;

        for (CartItem cart : cartItems) {

            Product product = cart.getProduct();

            if (product.getStock() < cart.getQuantity()) {
                throw new RuntimeException("Sản phẩm hết hàng");
            }

            product.setStock(product.getStock() - cart.getQuantity());
            productRepo.save(product);

            OrderItem item = new OrderItem();
            item.setOrder(order); // giờ order đã có ID
            item.setProduct(product);
            item.setQuantity(cart.getQuantity());
            item.setPrice(product.getPrice());

            orderItemRepo.save(item);

            total += product.getPrice() * cart.getQuantity();
        }

        order.setTotal(total);
        orderRepo.save(order);

        cartRepo.deleteByUser(user);

        return "redirect:/cart";
    }

    /* ===============================
       HELPER
    =============================== */
    private User getCurrentUser(Principal principal) {
        return userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}