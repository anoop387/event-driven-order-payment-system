package com.wellsfargo.order_service.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellsfargo.order_service.entity.Order;
import com.wellsfargo.order_service.kafka.event.OrderEventType;
import com.wellsfargo.order_service.kafka.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String ORDER_TOPIC = "orders";
    
    public void sendOrderCreatedEvent(Order order) {
        OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.from(order);
        try {
            String eventJson = objectMapper.writeValueAsString(orderCreatedEvent);
            kafkaTemplate.send(ORDER_TOPIC, order.getId().toString(), eventJson)
                    .whenComplete(
                            (result, failure) -> {
                                if (failure == null) {
                                    log.info("Order created event sent successfully for order: {}", order.getOrderNumber());
                                } else {
                                    log.error("Failed to send order created event for order: {}", order.getOrderNumber(), failure);
                                }
                            }
                    );
        } catch (JsonProcessingException e) {
            log.error("Error serializing order created event for order: {}", order.getOrderNumber(), e);
        }
    }
    
    public void publishOrderCreated(Order order) {
        sendOrderCreatedEvent(order);
    }
}
