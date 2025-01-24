package com.santanna.serviceorder.application.usecase;

import com.santanna.serviceorder.application.dto.OrderRequestDto;
import com.santanna.serviceorder.application.dto.OrderResponseDto;
import com.santanna.serviceorder.application.utils.LoggerUtils;
import com.santanna.serviceorder.application.mapper.OrderMapper;
import com.santanna.serviceorder.domain.repository.OrderRepository;
import com.santanna.serviceorder.interfaces.handler.model.BadRequestException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateOrderUseCase {

    private final OrderMapper orderMapper;
    private final LoggerUtils loggerUtils;
    private final OrderRepository orderRepository;

    public CreateOrderUseCase(OrderMapper orderMapper, LoggerUtils loggerUtils, OrderRepository orderRepository) {
        this.orderMapper = orderMapper;
        this.loggerUtils = loggerUtils;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponseDto execute(OrderRequestDto requestDto) {

        loggerUtils.logInfo(CreateOrderUseCase.class, "Starting order creation: {}", requestDto.getOrderNumber());

        boolean isOrderPresent = orderRepository.findByOrderNumber(requestDto.getOrderNumber()).isPresent();

        if (isOrderPresent){

            loggerUtils.logWarn(CreateOrderUseCase.class, "Duplicate order detected: {}", requestDto.getOrderNumber());
            throw new BadRequestException("Order already exists.");
        }

        var order = orderMapper.toDomain(requestDto);
        var savedOrder =orderRepository.save(order);

        loggerUtils.logInfo(CreateOrderUseCase.class, "Order created successfully. ID: {}", order.getId());

        return orderMapper.toDto(savedOrder);
    }
}
