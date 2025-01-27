package com.santanna.serviceorder.application.usecase;

import com.santanna.serviceorder.application.dto.OrderRequestDto;
import com.santanna.serviceorder.application.dto.OrderResponseDto;
import com.santanna.serviceorder.application.usecase.exception.BusinessException;
import com.santanna.serviceorder.application.utils.LoggerUtils;
import com.santanna.serviceorder.domain.model.Order;
import com.santanna.serviceorder.domain.model.OrderConverter;
import com.santanna.serviceorder.domain.model.OrderStatus;
import com.santanna.serviceorder.domain.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
    public OrderResponseDto execute(OrderRequestDto requestDto)   {


        boolean isOrderPresent = orderRepository.findByOrderNumber(requestDto.orderNumber()).isPresent();
        if (isOrderPresent){
            throw new BusinessException("Já existe um pedido com este número.");
        }

        var order = new Order(
                null,
                requestDto.orderNumber(),
                requestDto.productName(),
                requestDto.quantity(),
                requestDto.unitPrice(),
                OrderStatus.PROCESSED,
                LocalDateTime.now()
        );

        var savedOrder =orderRepository.save(order);

        loggerUtils.logInfo(CreateOrderUseCase.class, "Order created successfully. ID: {}", order.getId());

        return OrderConverter.toDto(savedOrder);
    }
}
