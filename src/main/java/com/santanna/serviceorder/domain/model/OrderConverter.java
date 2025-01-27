package com.santanna.serviceorder.domain.model;

import com.santanna.serviceorder.application.dto.OrderResponseDto;

public class OrderConverter {

    public static OrderResponseDto toDto(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getOrderNumber(),
                order.getProductName(),
                order.getQuantity(),
                order.getTotalValue(),
                order.getOrderStatus(),
                order.getCreatedAt()
        );
    }
}
