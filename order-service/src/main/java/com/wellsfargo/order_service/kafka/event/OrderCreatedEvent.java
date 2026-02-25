package com.wellsfargo.order_service.kafka.event;

import com.wellsfargo.order_service.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    
    private String eventId;
    private String orderNumber;
    private String customerId;
    private Double totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime eventTimestamp;
    private String source;
    
    public OrderCreatedEvent(Order order) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.orderNumber = order.getOrderNumber();
        this.customerId = order.getCustomerId();
        this.totalAmount = order.getTotalAmount();
        this.status = order.getStatus().toString();
        this.createdAt = order.getCreatedAt();
        this.eventTimestamp = LocalDateTime.now();
        this.source = "order-service";
    }
    
    public static OrderCreatedEvent from(Order order) {
        return new OrderCreatedEvent(order);
    }
}
