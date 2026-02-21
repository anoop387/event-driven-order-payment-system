package com.wellsfargo.order_service.service;

import com.wellsfargo.order_service.entity.Order;
import com.wellsfargo.order_service.repository.OrderRepository;
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
public class OrderService {
    
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    public Order createOrder(Order order) {
        log.info("Creating new order for customer: {}", order.getCustomerId());
        
        if (orderRepository.existsByOrderNumber(order.getOrderNumber())) {
            throw new IllegalArgumentException("Order with number " + order.getOrderNumber() + " already exists");
        }
        
        if (order.getOrderNumber() == null || order.getOrderNumber().isEmpty()) {
            order.setOrderNumber(generateOrderNumber());
        }
        
        if (order.getStatus() == null) {
            order.setStatus(Order.OrderStatus.PENDING);
        }
        
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());
        return savedOrder;
    }
    
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        log.debug("Fetching order by ID: {}", id);
        return orderRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Order> getOrderByOrderNumber(String orderNumber) {
        log.debug("Fetching order by order number: {}", orderNumber);
        return orderRepository.findByOrderNumber(orderNumber);
    }
    
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomerId(String customerId) {
        log.debug("Fetching orders for customer: {}", customerId);
        return orderRepository.findByCustomerId(customerId);
    }
    
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        log.debug("Fetching orders with status: {}", status);
        return orderRepository.findByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomerIdAndStatus(String customerId, Order.OrderStatus status) {
        log.debug("Fetching orders for customer: {} with status: {}", customerId, status);
        return orderRepository.findByCustomerIdAndStatus(customerId, status);
    }
    
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        log.debug("Fetching all orders");
        return orderRepository.findAll();
    }
    
    public Order updateOrderStatus(Long id, Order.OrderStatus status) {
        log.info("Updating order status for ID: {} to status: {}", id, status);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated successfully for ID: {}", id);
        return updatedOrder;
    }
    
    public Order updateOrder(Long id, Order orderDetails) {
        log.info("Updating order with ID: {}", id);
        
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        
        existingOrder.setCustomerId(orderDetails.getCustomerId());
        existingOrder.setTotalAmount(orderDetails.getTotalAmount());
        existingOrder.setStatus(orderDetails.getStatus());
        
        Order updatedOrder = orderRepository.save(existingOrder);
        log.info("Order updated successfully for ID: {}", id);
        return updatedOrder;
    }
    
    public void deleteOrder(Long id) {
        log.info("Deleting order with ID: {}", id);
        
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException("Order not found with ID: " + id);
        }
        
        orderRepository.deleteById(id);
        log.info("Order deleted successfully with ID: {}", id);
    }
    
    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
