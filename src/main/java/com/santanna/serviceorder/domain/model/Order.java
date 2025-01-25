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

    // Construtor privado para uso pelo builder
    private Order(Builder builder) {
        this.id = builder.id;
        this.orderNumber = builder.orderNumber;
        this.productName = builder.productName;
        this.quantity = builder.quantity;
        this.totalValue = builder.totalValue;
        this.orderStatus = builder.orderStatus;
        this.createdAt = builder.createdAt;
    }

    // Getters públicos (imutabilidade garantida)
    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public String getProductName() { return productName; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getTotalValue() { return totalValue; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Builder Pattern
    public static class Builder {
        private Long id;
        private String orderNumber;
        private String productName;
        private Integer quantity;
        private BigDecimal totalValue;
        private OrderStatus orderStatus;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder orderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
            return this;
        }

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder totalValue(BigDecimal unitPrice) {
            this.totalValue = unitPrice.multiply(BigDecimal.valueOf(quantity));
            return this;
        }

        public Builder orderStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }


}
