package com.santanna.serviceorder.application.usecase;

import com.santanna.serviceorder.application.dto.OrderResponseDto;
import com.santanna.serviceorder.application.usecase.exception.NotFoundException;
import com.santanna.serviceorder.application.utils.LoggerUtils;
import com.santanna.serviceorder.domain.common.PaginatedResult;
import com.santanna.serviceorder.domain.model.OrderConverter;
import com.santanna.serviceorder.domain.repository.OrderRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class GetOrderUseCase {


    private final LoggerUtils loggerUtils;
    private final OrderRepository orderRepository;

    public GetOrderUseCase(LoggerUtils loggerUtils, OrderRepository orderRepository) {
        this.loggerUtils = loggerUtils;
        this.orderRepository = orderRepository;
    }

    @Cacheable(value = "orders", key = "#id")
    public OrderResponseDto getById(String id) {
        loggerUtils.logInfo(GetOrderUseCase.class, "Fetching order by ID: {}", id);

        var order = orderRepository.findById(id).orElseThrow(()-> new NotFoundException("Order not found"));
        loggerUtils.logInfo(GetOrderUseCase.class, "Order found. ID: {}", id);

        return OrderConverter.toDto(order);
    }


    public PaginatedResult<OrderResponseDto> getAllOrders(int page, int size) {
        loggerUtils.logInfo(GetOrderUseCase.class, "Fetching all orders with pagination");
        var orders = orderRepository.findAll(page, size);

        loggerUtils.logInfo(GetOrderUseCase.class, "Retrieved {} orders successfully");

        return new PaginatedResult<>(
                orders.getContent().stream()
                        .map(OrderConverter::toDto)
                        .collect(Collectors.toList()),
                orders.getPageNumber(),
                orders.getPageSize(),
                orders.getTotalElements()
        );
    }
}
