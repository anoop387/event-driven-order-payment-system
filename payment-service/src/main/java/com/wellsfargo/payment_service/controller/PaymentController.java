package com.wellsfargo.payment_service.controller;

import com.wellsfargo.payment_service.entity.Payment;
import com.wellsfargo.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
//@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class PaymentController {
    
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody Payment payment) {
        log.info("REST request to create payment: {}", payment);
        Payment createdPayment = paymentService.createPayment(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        log.info("REST request to get payment by ID: {}", id);
        Optional<Payment> payment = paymentService.getPaymentById(id);
        return payment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/payment-number/{paymentNumber}")
    public ResponseEntity<Payment> getPaymentByPaymentNumber(@PathVariable String paymentNumber) {
        log.info("REST request to get payment by payment number: {}", paymentNumber);
        Optional<Payment> payment = paymentService.getPaymentByPaymentNumber(paymentNumber);
        return payment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/order/{orderNumber}")
    public ResponseEntity<List<Payment>> getPaymentsByOrderNumber(@PathVariable String orderNumber) {
        log.info("REST request to get payments for order: {}", orderNumber);
        List<Payment> payments = paymentService.getPaymentsByOrderNumber(orderNumber);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Payment>> getPaymentsByCustomerId(@PathVariable String customerId) {
        log.info("REST request to get payments for customer: {}", customerId);
        List<Payment> payments = paymentService.getPaymentsByCustomerId(customerId);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable Payment.PaymentStatus status) {
        log.info("REST request to get payments with status: {}", status);
        List<Payment> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/method/{paymentMethod}")
    public ResponseEntity<List<Payment>> getPaymentsByPaymentMethod(@PathVariable Payment.PaymentMethod paymentMethod) {
        log.info("REST request to get payments with method: {}", paymentMethod);
        List<Payment> payments = paymentService.getPaymentsByPaymentMethod(paymentMethod);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/customer/{customerId}/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByCustomerIdAndStatus(
            @PathVariable String customerId, 
            @PathVariable Payment.PaymentStatus status) {
        log.info("REST request to get payments for customer: {} with status: {}", customerId, status);
        List<Payment> payments = paymentService.getPaymentsByCustomerIdAndStatus(customerId, status);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/order/{orderNumber}/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByOrderNumberAndStatus(
            @PathVariable String orderNumber, 
            @PathVariable Payment.PaymentStatus status) {
        log.info("REST request to get payments for order: {} with status: {}", orderNumber, status);
        List<Payment> payments = paymentService.getPaymentsByOrderNumberAndStatus(orderNumber, status);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        log.info("REST request to get all payments");
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable Long id, 
            @RequestParam Payment.PaymentStatus status) {
        log.info("REST request to update payment status for ID: {} to status: {}", id, status);
        Payment updatedPayment = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(updatedPayment);
    }
    
    @PostMapping("/{id}/process")
    public ResponseEntity<Payment> processPayment(@PathVariable Long id) {
        log.info("REST request to process payment with ID: {}", id);
        Payment processedPayment = paymentService.processPayment(id);
        return ResponseEntity.ok(processedPayment);
    }
    
    @PostMapping("/{id}/complete")
    public ResponseEntity<Payment> completePayment(
            @PathVariable Long id, 
            @RequestParam String transactionId) {
        log.info("REST request to complete payment with ID: {} and transaction ID: {}", id, transactionId);
        Payment completedPayment = paymentService.completePayment(id, transactionId);
        return ResponseEntity.ok(completedPayment);
    }
    
    @PostMapping("/{id}/fail")
    public ResponseEntity<Payment> failPayment(
            @PathVariable Long id, 
            @RequestParam(required = false) String reason) {
        log.info("REST request to fail payment with ID: {}. Reason: {}", id, reason);
        Payment failedPayment = paymentService.failPayment(id, reason != null ? reason : "Payment processing failed");
        return ResponseEntity.ok(failedPayment);
    }
    
    @PostMapping("/{id}/refund")
    public ResponseEntity<Payment> refundPayment(@PathVariable Long id) {
        log.info("REST request to refund payment with ID: {}", id);
        Payment refundedPayment = paymentService.refundPayment(id);
        return ResponseEntity.ok(refundedPayment);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(
            @PathVariable Long id, 
            @Valid @RequestBody Payment paymentDetails) {
        log.info("REST request to update payment with ID: {}", id);
        Payment updatedPayment = paymentService.updatePayment(id, paymentDetails);
        return ResponseEntity.ok(updatedPayment);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        log.info("REST request to delete payment with ID: {}", id);
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
