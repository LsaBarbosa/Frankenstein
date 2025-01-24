package com.santanna.serviceorder.application.usecase;

import com.santanna.serviceorder.application.utils.LoggerUtils;
import com.santanna.serviceorder.application.mapper.OrderMapper;
import com.santanna.serviceorder.domain.repository.OrderRepository;
import com.santanna.serviceorder.interfaces.handler.model.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class DeleteOrderUseCase {

    private final OrderMapper orderMapper;
    private final LoggerUtils loggerUtils;
    private final OrderRepository orderRepository;

    public DeleteOrderUseCase(OrderMapper orderMapper, LoggerUtils loggerUtils, OrderRepository orderRepository) {
        this.orderMapper = orderMapper;
        this.loggerUtils = loggerUtils;
        this.orderRepository = orderRepository;
    }


    @Transactional
    @CacheEvict(value = "orders", key = "#id")
    public void execute(Long id) {
        loggerUtils.logInfo(DeleteOrderUseCase.class, "Attempting to delete order with ID: {}", id);

        orderRepository.findById(id)
                .orElseThrow(() -> {
                    loggerUtils.logWarn(DeleteOrderUseCase.class, "Order with ID {} not found", id);
                    return new NotFoundException("Order not found with ID: " + id);
                });

        orderRepository.deleteById(id);
        loggerUtils.logInfo(DeleteOrderUseCase.class, "Order with ID {} deleted successfully", id);
    }

}
