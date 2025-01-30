package com.santanna.serviceorder.domain.builder.objectbuild;

import com.santanna.serviceorder.domain.builder.OrderBuilder;
import com.santanna.serviceorder.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateOrderBuilder extends OrderBuilder {

    @Override
    public void buildId(String id) {
    }

    @Override
    public void buildOrderNumber(String orderNumber) {
        orderBuilder.setOrderNumber(orderNumber);
    }

    @Override
    public void buildProductName(String productName) {
        orderBuilder.setProductName(productName);

    }

    @Override
    public void buildQuantity(Integer quantity) {
        orderBuilder.setQuantity(quantity);
    }

    @Override
    public void buildTotalValue(BigDecimal totalValue) {
        orderBuilder.setTotalValue(totalValue);
    }

    @Override
    public void buildOrderStatus(OrderStatus status) {
        orderBuilder.setOrderStatus(status);
    }

    @Override
    public void buildCreatedAt(LocalDateTime createdAt) {
        orderBuilder.setCreatedAt(createdAt);
    }
}
