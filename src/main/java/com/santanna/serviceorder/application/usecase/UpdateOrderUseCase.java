package com.santanna.serviceorder.application.usecase;

import com.santanna.serviceorder.application.dto.OrderResponseDto;
import com.santanna.serviceorder.application.utils.LoggerUtils;
import com.santanna.serviceorder.application.mapper.OrderMapper;
import com.santanna.serviceorder.domain.model.OrderStatus;
import com.santanna.serviceorder.domain.repository.OrderRepository;
import com.santanna.serviceorder.interfaces.handler.model.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class UpdateOrderUseCase {

    private final OrderMapper orderMapper;
    private final LoggerUtils loggerUtils;
    private final OrderRepository orderRepository;

    public UpdateOrderUseCase(OrderMapper orderMapper, LoggerUtils loggerUtils, OrderRepository orderRepository) {
        this.orderMapper = orderMapper;
        this.loggerUtils = loggerUtils;
        this.orderRepository = orderRepository;
    }

    @Transactional
    @CacheEvict(value = "orders", key = "#id", allEntries = true)
    public OrderResponseDto execute(Long id, String newStatus) {
        loggerUtils.logInfo(UpdateOrderUseCase.class, "Updating order status. ID: {}, New Status: {}", id, newStatus);

        var order = orderRepository.findById(id).orElseThrow(() -> {
            loggerUtils.logWarn(UpdateOrderUseCase.class, "Order with ID {} not found", id);
            return new NotFoundException("Order not found");
        });

        order.updateStatus(OrderStatus.valueOf(newStatus));
        var updatedOrder = orderRepository.save(order);

        loggerUtils.logInfo(UpdateOrderUseCase.class, "Order status updated successfully. ID: {}, New Status: {}", id, newStatus);
        return orderMapper.toDto(updatedOrder);
    }
}
