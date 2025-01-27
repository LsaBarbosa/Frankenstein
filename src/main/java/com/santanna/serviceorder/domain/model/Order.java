package com.santanna.serviceorder.domain.model;

import com.santanna.serviceorder.domain.exception.DomainException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private Long id;
    private String orderNumber;
    private String productName;
    private Integer quantity;
    private BigDecimal totalValue;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;

    public Order(Long id, String orderNumber, String productName, Integer quantity, BigDecimal unitPrice, OrderStatus orderStatus, LocalDateTime createdAt) {
        if (quantity <= 0) {
            throw new DomainException("A quantidade do pedido deve ser maior que zero.");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("O preço unitário deve ser maior que zero.");
        }
        this.id = id;
        this.orderNumber = orderNumber;
        this.productName = productName;
        this.quantity = quantity;
        this.totalValue = calculateTotalValue(unitPrice, quantity);
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
    }

    // Métodos fábrica estáticos para facilitar a criação
    public static Order of(Long id, String orderNumber, String productName, Integer quantity, BigDecimal unitPrice, OrderStatus orderStatus, LocalDateTime createdAt) {
        return new Order(id, orderNumber, productName, quantity, unitPrice, orderStatus, createdAt);
    }

    // Getters públicos
    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public String getProductName() { return productName; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getTotalValue() { return totalValue; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void updateStatus(OrderStatus newStatus) {
        this.orderStatus = newStatus;
    }

    private BigDecimal calculateTotalValue(BigDecimal unitPrice, Integer quantity) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

}
