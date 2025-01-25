package com.santanna.serviceorder.domain.model;

import com.santanna.serviceorder.application.dto.OrderRequestDto;
import com.santanna.serviceorder.application.dto.OrderResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderConverter {

    public static Order fromDto(OrderRequestDto dto) {
        return new Order.Builder()
                .orderNumber(dto.getOrderNumber())
                .productName(dto.getProductName())
                .quantity(dto.getQuantity())
                .totalValue(dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getQuantity())))
                .createdAt(LocalDateTime.now())
                .orderStatus(OrderStatus.RECEIVED)
                .build();
    }

    public static OrderResponseDto toDto(Order order) {
        return OrderResponseDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .productName(order.getProductName())
                .quantity(order.getQuantity())
                .totalValue(order.getTotalValue())
                .status(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
