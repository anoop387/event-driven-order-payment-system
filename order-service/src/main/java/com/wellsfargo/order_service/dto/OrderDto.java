package com.wellsfargo.order_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    
    private Long id;
    
    @NotBlank(message = "Order number is required")
    private String orderNumber;
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private Double totalAmount;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
