package com.gunwala.repo.gunwala;

import com.gunwala.model.entitities.gunwala.RazorpayPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RazorpayPaymentRepository extends JpaRepository<RazorpayPayment, Integer> {

    Optional<RazorpayPayment> findByRazorpayPaymentId(String razorpayPaymentId);

    List<RazorpayPayment> findByUserId(int userId);

    List<RazorpayPayment> findByRazorpayOrderId(String razorpayOrderId);

    List<RazorpayPayment> findByStatus(String status);

    List<RazorpayPayment> findByOrderId(int orderId);
}
