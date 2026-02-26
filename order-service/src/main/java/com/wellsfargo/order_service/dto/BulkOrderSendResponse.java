package com.wellsfargo.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkOrderSendResponse {
    
    private int totalOrders;
    private int successfulSends;
    private int failedSends;
    private List<OrderResponse> successfulOrders;
    private List<OrderErrorInfo> failedOrders;
    private String message;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderErrorInfo {
        private Long orderId;
        private String orderNumber;
        private String errorMessage;
    }
}
