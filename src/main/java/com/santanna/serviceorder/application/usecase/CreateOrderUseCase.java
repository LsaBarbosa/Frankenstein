package com.santanna.serviceorder.application.usecase;

import com.santanna.serviceorder.application.dto.OrderRequestDto;
import com.santanna.serviceorder.application.dto.OrderResponseDto;
import com.santanna.serviceorder.application.usecase.exception.OrderAlreadyExistsException;
import com.santanna.serviceorder.application.utils.LoggerUtils;
import com.santanna.serviceorder.domain.model.Order;
import com.santanna.serviceorder.domain.model.OrderConverter;
import com.santanna.serviceorder.domain.model.OrderStatus;
import com.santanna.serviceorder.domain.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CreateOrderUseCase {

    private final LoggerUtils loggerUtils;
    private final OrderRepository orderRepository;

    public CreateOrderUseCase( LoggerUtils loggerUtils, OrderRepository orderRepository) {
        this.loggerUtils = loggerUtils;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponseDto execute(OrderRequestDto requestDto) throws OrderAlreadyExistsException {

        loggerUtils.logInfo(CreateOrderUseCase.class, "Starting order creation: {}", requestDto.getOrderNumber());

        boolean isOrderPresent = orderRepository.findByOrderNumber(requestDto.getOrderNumber()).isPresent();
        if (isOrderPresent){
            loggerUtils.logWarn(CreateOrderUseCase.class, "Duplicate order detected: {}", requestDto.getOrderNumber());
            throw new OrderAlreadyExistsException("Order already exists.");
        }


        var order = new Order.Builder()
                .orderNumber(requestDto.getOrderNumber())
                .productName(requestDto.getProductName())
                .quantity(requestDto.getQuantity())
                .totalValue(requestDto.getUnitPrice()
                        .multiply(BigDecimal.valueOf(requestDto.getQuantity())))
                .orderStatus(OrderStatus.PROCESSED).createdAt(LocalDateTime.now()).build();
        var savedOrder =orderRepository.save(order);

        loggerUtils.logInfo(CreateOrderUseCase.class, "Order created successfully. ID: {}", order.getId());

        return OrderConverter.toDto(savedOrder);
    }
}
