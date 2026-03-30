package com.example.medcare.repository;

import com.example.medcare.model.CartItem;
import com.example.medcare.model.Product;
import com.example.medcare.model.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(User user);

    CartItem findByUserAndProduct(User user, Product product);

    void deleteByUser(User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.user.id = :userId AND c.product.id = :productId")
    void deleteByUserIdAndProductId(@Param("userId") Long userId,
                                    @Param("productId") Long productId);
}