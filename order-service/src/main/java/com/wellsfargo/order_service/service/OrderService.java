package com.wellsfargo.order_service.service;

import com.wellsfargo.order_service.dto.OrderRequest;
import com.wellsfargo.order_service.dto.OrderResponse;
import com.wellsfargo.order_service.entity.Order;
import com.wellsfargo.order_service.kafka.event.OrderEventType;
import com.wellsfargo.order_service.kafka.producer.OrderEventProducer;
import com.wellsfargo.order_service.mapper.OrderMapper;
import com.wellsfargo.order_service.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
//@RequiredArgsConstructor
@Transactional
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class OrderService {
    
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private final OrderMapper orderMapper;
    
    public OrderService(OrderRepository orderRepository, OrderEventProducer orderEventProducer, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderEventProducer = orderEventProducer;
        this.orderMapper = orderMapper;
    }
    public OrderResponse createOrder(OrderRequest orderRequest) {
        log.info("Creating new order for customer: {}", orderRequest.getCustomerId());
        
        Order order = orderMapper.toEntityFromRequest(orderRequest);
        
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
        
        // Publish order created event
        orderEventProducer.sendOrderCreatedEvent(savedOrder);
        
        return orderMapper.toResponse(savedOrder, "Order created successfully");
    }
    
    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrderById(Long id) {
        log.debug("Fetching order by ID: {}", id);
        return orderRepository.findById(id)
                .map(orderMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrderByOrderNumber(String orderNumber) {
        log.debug("Fetching order by order number: {}", orderNumber);
        return orderRepository.findByOrderNumber(orderNumber)
                .map(orderMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomerId(String customerId) {
        log.debug("Fetching orders for customer: {}", customerId);
        return orderRepository.findByCustomerId(customerId).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(Order.OrderStatus status) {
        log.debug("Fetching orders with status: {}", status);
        return orderRepository.findByStatus(status).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomerIdAndStatus(String customerId, Order.OrderStatus status) {
        log.debug("Fetching orders for customer: {} with status: {}", customerId, status);
        return orderRepository.findByCustomerIdAndStatus(customerId, status).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        log.debug("Fetching all orders");
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public OrderResponse updateOrderStatus(Long id, Order.OrderStatus status) {
        log.info("Updating order status for ID: {} to status: {}", id, status);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated successfully for ID: {}", id);
        
        // Publish order status update event
        String eventType = switch (status) {
            case CONFIRMED -> OrderEventType.ORDER_CONFIRMED;
            case SHIPPED -> OrderEventType.ORDER_SHIPPED;
            case DELIVERED -> OrderEventType.ORDER_DELIVERED;
            case CANCELLED -> OrderEventType.ORDER_CANCELLED;
            default -> OrderEventType.ORDER_UPDATED;
        };
        orderEventProducer.sendOrderCreatedEvent(updatedOrder);
        
        return orderMapper.toResponse(updatedOrder, "Order status updated successfully");
    }
    
    public OrderResponse updateOrder(Long id, OrderRequest orderRequest) {
        log.info("Updating order with ID: {}", id);
        
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        
        orderMapper.updateEntityFromRequest(orderRequest, existingOrder);
        
        Order updatedOrder = orderRepository.save(existingOrder);
        log.info("Order updated successfully for ID: {}", id);
        
        // Publish order updated event
        orderEventProducer.sendOrderCreatedEvent(updatedOrder);
        
        return orderMapper.toResponse(updatedOrder, "Order updated successfully");
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
