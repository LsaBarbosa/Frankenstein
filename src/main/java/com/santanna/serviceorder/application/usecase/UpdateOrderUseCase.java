package com.santanna.serviceorder.application.usecase;

import com.santanna.serviceorder.application.dto.OrderResponseDto;
import com.santanna.serviceorder.application.usecase.exception.NotFoundException;
import com.santanna.serviceorder.application.utils.LoggerUtils;
import com.santanna.serviceorder.domain.model.OrderConverter;
import com.santanna.serviceorder.domain.model.OrderStatus;
import com.santanna.serviceorder.domain.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class UpdateOrderUseCase {

    private final LoggerUtils loggerUtils;
    private final OrderRepository orderRepository;

    public UpdateOrderUseCase(LoggerUtils loggerUtils, OrderRepository orderRepository) {
        this.loggerUtils = loggerUtils;
        this.orderRepository = orderRepository;
    }

    @Transactional
    @CacheEvict(value = "orders", key = "#id", allEntries = true)
    public OrderResponseDto execute(Long id, OrderStatus newStatus) {
        loggerUtils.logInfo(UpdateOrderUseCase.class, "Updating order status. ID: {}, New Status: {}", id, newStatus);

        var order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        loggerUtils.logWarn(UpdateOrderUseCase.class, "Order with ID {} not found", id);


        order.updateStatus(newStatus);
        var updatedOrder = orderRepository.save(order);

        loggerUtils.logInfo(UpdateOrderUseCase.class, "Order status updated successfully. ID: {}, New Status: {}", id, newStatus);
        return OrderConverter.toDto(updatedOrder);
    }
}
