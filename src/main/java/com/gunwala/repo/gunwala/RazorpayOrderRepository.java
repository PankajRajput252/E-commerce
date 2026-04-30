package com.gunwala.repo.gunwala;

import com.gunwala.model.entitities.gunwala.RazorpayOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface RazorpayOrderRepository extends JpaRepository<RazorpayOrder, Integer> {

    Optional<RazorpayOrder> findByRazorpayOrderId(String razorpayOrderId);

    List<RazorpayOrder> findByUserId(int userId);

    List<RazorpayOrder> findByProductId(int productId);

    List<RazorpayOrder> findByStatus(String status);

    boolean existsByRazorpayOrderId(String razorpayOrderId);
}
