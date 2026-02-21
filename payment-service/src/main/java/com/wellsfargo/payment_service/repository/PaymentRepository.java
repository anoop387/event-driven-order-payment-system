package com.wellsfargo.payment_service.repository;

import com.wellsfargo.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByPaymentNumber(String paymentNumber);
    
    List<Payment> findByOrderNumber(String orderNumber);
    
    List<Payment> findByCustomerId(String customerId);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);
    
    List<Payment> findByCustomerIdAndStatus(String customerId, Payment.PaymentStatus status);
    
    List<Payment> findByOrderNumberAndStatus(String orderNumber, Payment.PaymentStatus status);
    
    boolean existsByPaymentNumber(String paymentNumber);
}
