package com.santanna.serviceorder.domain.builder;

import com.santanna.serviceorder.domain.model.Order;
import com.santanna.serviceorder.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDirector {

    private OrderBuilder builder;

    public OrderDirector(OrderBuilder builder) {
        this.builder = builder;
    }

    public void getBuilder(Long id, String orderNumber, String productName, Integer quantity, BigDecimal totalValue,
                           OrderStatus status, LocalDateTime createdAt) {
        builder.buildId(id);
        builder.buildOrderNumber(orderNumber);
        builder.buildProductName(productName);
        builder.buildQuantity(quantity);
        builder.buildTotalValue(totalValue);
        builder.buildOrderStatus(status);
        builder.buildCreatedAt(createdAt);

    }

    public Order getOrderBuilder() {
        return builder.getOrderBuilder();
    }
}
