package com.wellsfargo.order_service.repository;

import com.wellsfargo.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByCustomerId(String customerId);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    List<Order> findByCustomerIdAndStatus(String customerId, Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId AND o.status = :status ORDER BY o.createdAt DESC")
    List<Order> findByCustomerIdAndStatusOrderByCreatedAtDesc(@Param("customerId") String customerId, 
                                                             @Param("status") Order.OrderStatus status);
    
    boolean existsByOrderNumber(String orderNumber);
}
