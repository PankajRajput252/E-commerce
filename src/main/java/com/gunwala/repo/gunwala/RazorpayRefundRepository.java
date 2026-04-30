package com.gunwala.repo.gunwala;

import com.gunwala.model.entitities.gunwala.RazorpayRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RazorpayRefundRepository extends JpaRepository<RazorpayRefund, Integer> {

    Optional<RazorpayRefund> findByRazorpayRefundId(String razorpayRefundId);

    List<RazorpayRefund> findByUserId(int userId);

    List<RazorpayRefund> findByOrderId(int orderId);

    List<RazorpayRefund> findByPaymentId(int paymentId);

    List<RazorpayRefund> findByStatus(String status);
}
