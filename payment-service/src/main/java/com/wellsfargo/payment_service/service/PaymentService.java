package com.wellsfargo.payment_service.service;

import com.wellsfargo.payment_service.entity.Payment;
import com.wellsfargo.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
//@RequiredArgsConstructor
@Transactional
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class PaymentService {
    
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment createPayment(Payment payment) {
        log.info("Creating new payment for order: {}", payment.getOrderNumber());
        
        if (paymentRepository.existsByPaymentNumber(payment.getPaymentNumber())) {
            throw new IllegalArgumentException("Payment with number " + payment.getPaymentNumber() + " already exists");
        }
        
        if (payment.getPaymentNumber() == null || payment.getPaymentNumber().isEmpty()) {
            payment.setPaymentNumber(generatePaymentNumber());
        }
        
        if (payment.getStatus() == null) {
            payment.setStatus(Payment.PaymentStatus.PENDING);
        }
        
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created successfully with ID: {}", savedPayment.getId());
        return savedPayment;
    }
    
    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentById(Long id) {
        log.debug("Fetching payment by ID: {}", id);
        return paymentRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentByPaymentNumber(String paymentNumber) {
        log.debug("Fetching payment by payment number: {}", paymentNumber);
        return paymentRepository.findByPaymentNumber(paymentNumber);
    }
    
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByOrderNumber(String orderNumber) {
        log.debug("Fetching payments for order: {}", orderNumber);
        return paymentRepository.findByOrderNumber(orderNumber);
    }
    
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByCustomerId(String customerId) {
        log.debug("Fetching payments for customer: {}", customerId);
        return paymentRepository.findByCustomerId(customerId);
    }
    
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByStatus(Payment.PaymentStatus status) {
        log.debug("Fetching payments with status: {}", status);
        return paymentRepository.findByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByPaymentMethod(Payment.PaymentMethod paymentMethod) {
        log.debug("Fetching payments with method: {}", paymentMethod);
        return paymentRepository.findByPaymentMethod(paymentMethod);
    }
    
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByCustomerIdAndStatus(String customerId, Payment.PaymentStatus status) {
        log.debug("Fetching payments for customer: {} with status: {}", customerId, status);
        return paymentRepository.findByCustomerIdAndStatus(customerId, status);
    }
    
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByOrderNumberAndStatus(String orderNumber, Payment.PaymentStatus status) {
        log.debug("Fetching payments for order: {} with status: {}", orderNumber, status);
        return paymentRepository.findByOrderNumberAndStatus(orderNumber, status);
    }
    
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        log.debug("Fetching all payments");
        return paymentRepository.findAll();
    }
    
    public Payment updatePaymentStatus(Long id, Payment.PaymentStatus status) {
        log.info("Updating payment status for ID: {} to status: {}", id, status);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + id));
        
        payment.setStatus(status);
        Payment updatedPayment = paymentRepository.save(payment);
        log.info("Payment status updated successfully for ID: {}", id);
        return updatedPayment;
    }
    
    public Payment processPayment(Long id) {
        log.info("Processing payment with ID: {}", id);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + id));
        
        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment cannot be processed. Current status: " + payment.getStatus());
        }
        
        payment.setStatus(Payment.PaymentStatus.PROCESSING);
        Payment updatedPayment = paymentRepository.save(payment);
        
        // Simulate payment processing
        simulatePaymentProcessing(updatedPayment);
        
        log.info("Payment processed successfully for ID: {}", id);
        return updatedPayment;
    }
    
    public Payment completePayment(Long id, String transactionId) {
        log.info("Completing payment with ID: {} and transaction ID: {}", id, transactionId);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + id));
        
        if (payment.getStatus() != Payment.PaymentStatus.PROCESSING) {
            throw new IllegalStateException("Payment cannot be completed. Current status: " + payment.getStatus());
        }
        
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setTransactionId(transactionId);
        Payment updatedPayment = paymentRepository.save(payment);
        
        log.info("Payment completed successfully for ID: {}", id);
        return updatedPayment;
    }
    
    public Payment failPayment(Long id, String reason) {
        log.info("Failing payment with ID: {}. Reason: {}", id, reason);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + id));
        
        payment.setStatus(Payment.PaymentStatus.FAILED);
        Payment updatedPayment = paymentRepository.save(payment);
        
        log.info("Payment failed for ID: {}. Reason: {}", id, reason);
        return updatedPayment;
    }
    
    public Payment refundPayment(Long id) {
        log.info("Refunding payment with ID: {}", id);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + id));
        
        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Payment cannot be refunded. Current status: " + payment.getStatus());
        }
        
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        Payment updatedPayment = paymentRepository.save(payment);
        
        log.info("Payment refunded successfully for ID: {}", id);
        return updatedPayment;
    }
    
    public Payment updatePayment(Long id, Payment paymentDetails) {
        log.info("Updating payment with ID: {}", id);
        
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + id));
        
        existingPayment.setOrderNumber(paymentDetails.getOrderNumber());
        existingPayment.setCustomerId(paymentDetails.getCustomerId());
        existingPayment.setAmount(paymentDetails.getAmount());
        existingPayment.setPaymentMethod(paymentDetails.getPaymentMethod());
        existingPayment.setTransactionId(paymentDetails.getTransactionId());
        
        Payment updatedPayment = paymentRepository.save(existingPayment);
        log.info("Payment updated successfully for ID: {}", id);
        return updatedPayment;
    }
    
    public void deletePayment(Long id) {
        log.info("Deleting payment with ID: {}", id);
        
        if (!paymentRepository.existsById(id)) {
            throw new IllegalArgumentException("Payment not found with ID: " + id);
        }
        
        paymentRepository.deleteById(id);
        log.info("Payment deleted successfully with ID: {}", id);
    }
    
    private String generatePaymentNumber() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private void simulatePaymentProcessing(Payment payment) {
        // This would integrate with actual payment gateway
        // For now, we'll simulate with a simple success/failure logic
        try {
            Thread.sleep(1000); // Simulate processing time
            // 90% success rate
            if (Math.random() > 0.1) {
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
                payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
            } else {
                payment.setStatus(Payment.PaymentStatus.FAILED);
            }
            paymentRepository.save(payment);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            payment.setStatus(Payment.PaymentStatus.FAILED);
            paymentRepository.save(payment);
        }
    }
}
