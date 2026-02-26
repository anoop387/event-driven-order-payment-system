package com.wellsfargo.payment_service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellsfargo.payment_service.kafka.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {
    
    private final ObjectMapper objectMapper;
    
    private static final String ORDER_TOPIC = "orders";
    private static final String CONSUMER_GROUP = "payment-service-group";
    
    @KafkaListener(
        topics = ORDER_TOPIC,
        groupId = CONSUMER_GROUP,
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderCreatedEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            log.info("Received order event from topic: {}, partition: {}, offset: {}", topic, partition, offset);
            
            OrderCreatedEvent orderCreatedEvent = objectMapper.readValue(message, OrderCreatedEvent.class);
            
            log.info("Processing order created event for order: {}, customer: {}, amount: {}", 
                    orderCreatedEvent.getOrderNumber(), 
                    orderCreatedEvent.getCustomerId(), 
                    orderCreatedEvent.getTotalAmount());
            
            // Process the order event (e.g., initiate payment)
            processOrderPayment(orderCreatedEvent);
            
            // Acknowledge the message
            acknowledgment.acknowledge();
            log.info("Successfully processed and acknowledged order event for order: {}", 
                    orderCreatedEvent.getOrderNumber());
            
        } catch (JsonProcessingException e) {
            log.error("Error deserializing order event message: {}", message, e);
            // In a real application, you might want to move this to a dead-letter topic
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing order event", e);
            // Don't acknowledge so the message can be retried
            throw e;
        }
    }
    
    private void processOrderPayment(OrderCreatedEvent orderCreatedEvent) {
        // TODO: Implement payment processing logic
        // This is where you would:
        // 1. Create a payment record
        // 2. Initiate payment gateway integration
        // 3. Update payment status
        // 4. Publish payment events
        
        log.info("Initiating payment processing for order: {}, amount: {}", 
                orderCreatedEvent.getOrderNumber(), 
                orderCreatedEvent.getTotalAmount());
        
        // Simulate payment processing
        try {
            Thread.sleep(1000); // Simulate processing time
            log.info("Payment processing completed for order: {}", orderCreatedEvent.getOrderNumber());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Payment processing interrupted for order: {}", orderCreatedEvent.getOrderNumber());
        }
    }
}
