package com.wellsfargo.order_service.mapper;

import com.wellsfargo.order_service.dto.OrderDto;
import com.wellsfargo.order_service.dto.OrderRequest;
import com.wellsfargo.order_service.dto.OrderResponse;
import com.wellsfargo.order_service.entity.Order;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface OrderMapper {
    
    @Mapping(target = "status", expression = "java(orderStatusToString(order.getStatus()))")
    OrderDto toDto(Order order);
    
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToOrderStatus")
    Order toEntity(OrderDto orderDto);
    
    @Mapping(target = "status", expression = "java(orderStatusToString(order.getStatus()))")
    OrderResponse toResponse(Order order);
    
    @AfterMapping
    default void setMessage(OrderResponse response, @MappingTarget OrderResponse target, String message) {
        if (response != null && message != null) {
            target.setMessage(message);
        }
    }
    
    default OrderResponse toResponse(Order order, String message) {
        OrderResponse response = toResponse(order);
        if (response != null) {
            response.setMessage(message);
        }
        return response;
    }
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToOrderStatus")
    Order toEntityFromRequest(OrderRequest orderRequest);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToOrderStatus")
    void updateEntityFromRequest(OrderRequest orderRequest, @MappingTarget Order order);
    
    @Named("stringToOrderStatus")
    default Order.OrderStatus stringToOrderStatus(String status) {
        if (status == null) {
            return null;
        }
        try {
            return Order.OrderStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
    }
    
    default String orderStatusToString(Order.OrderStatus status) {
        return status != null ? status.toString() : null;
    }
}
