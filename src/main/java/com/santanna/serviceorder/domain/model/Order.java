package com.santanna.serviceorder.domain.model;

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

    public void updateStatus(OrderStatus newStatus) {
        this.orderStatus = newStatus;
    }

    public BigDecimal calculateTotalValue(BigDecimal unitPrice) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Order() {
    }

    public Order(Long id, String orderNumber, String productName, Integer quantity, BigDecimal totalValue, OrderStatus orderStatus, LocalDateTime createdAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.productName = productName;
        this.quantity = quantity;
        this.totalValue = totalValue;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
    }

    public Order(String orderNumber, String productName, Integer quantity, BigDecimal totalValue) {
        this.orderNumber = orderNumber;
        this.productName = productName;
        this.quantity = quantity;
        this.totalValue = totalValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
