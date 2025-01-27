package com.santanna.serviceorder.domain.builder;

import com.santanna.serviceorder.domain.model.Order;
import com.santanna.serviceorder.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class OrderBuilder {

    // instancia que será criada com builder
    protected Order orderBuilder;

    public OrderBuilder() {
        orderBuilder = new Order();
    }

    public Order getOrderBuilder(){
        return orderBuilder;
    }

    // será usado na criação do objeto
    // retirar o abstract tira a obrigatoriedade de implementar
    public  abstract void buildId(Long id);
    public abstract void buildOrderNumber(String orderNumber);
    public abstract void buildProductName(String productName);
    public abstract void buildQuantity(Integer quantity);
    public abstract void buildTotalValue(BigDecimal totalValue);
    public abstract void buildOrderStatus(OrderStatus status);
    public abstract void buildCreatedAt(LocalDateTime createdAt);



}
