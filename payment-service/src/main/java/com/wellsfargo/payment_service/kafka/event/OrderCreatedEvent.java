package com.wellsfargo.payment_service.kafka.event;

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
}
