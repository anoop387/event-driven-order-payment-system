package com.wellsfargo.order_service.controller;

import com.wellsfargo.order_service.dto.OrderRequest;
import com.wellsfargo.order_service.dto.OrderResponse;
import com.wellsfargo.order_service.entity.Order;
import com.wellsfargo.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {
    
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        log.info("REST request to create order: {}", orderRequest);
        OrderResponse createdOrder = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        log.info("REST request to get order by ID: {}", id);
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/order-number/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByOrderNumber(@PathVariable String orderNumber) {
        log.info("REST request to get order by order number: {}", orderNumber);
        return orderService.getOrderByOrderNumber(orderNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerId(@PathVariable String customerId) {
        log.info("REST request to get orders for customer: {}", customerId);
        List<OrderResponse> orderResponses = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orderResponses);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable Order.OrderStatus status) {
        log.info("REST request to get orders with status: {}", status);
        List<OrderResponse> orderResponses = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orderResponses);
    }
    
    @GetMapping("/customer/{customerId}/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerIdAndStatus(
            @PathVariable String customerId, 
            @PathVariable Order.OrderStatus status) {
        log.info("REST request to get orders for customer: {} with status: {}", customerId, status);
        List<OrderResponse> orderResponses = orderService.getOrdersByCustomerIdAndStatus(customerId, status);
        return ResponseEntity.ok(orderResponses);
    }
    
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("REST request to get all orders");
        List<OrderResponse> orderResponses = orderService.getAllOrders();
        return ResponseEntity.ok(orderResponses);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id, 
            @RequestParam Order.OrderStatus status) {
        log.info("REST request to update order status for ID: {} to status: {}", id, status);
        OrderResponse updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long id, 
            @Valid @RequestBody OrderRequest orderRequest) {
        log.info("REST request to update order with ID: {}", id);
        OrderResponse updatedOrder = orderService.updateOrder(id, orderRequest);
        return ResponseEntity.ok(updatedOrder);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.info("REST request to delete order with ID: {}", id);
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
