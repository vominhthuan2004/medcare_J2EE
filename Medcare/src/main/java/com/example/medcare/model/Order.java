package com.example.medcare.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;

    private double total;

    private String receiverName;
    private String phone;
    private String address;

    // ✅ Thêm field status
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public enum OrderStatus {
        PENDING,
        SHIPPING,
        DELIVERED
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // ✅ Chỉ giữ 1 quan hệ
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}