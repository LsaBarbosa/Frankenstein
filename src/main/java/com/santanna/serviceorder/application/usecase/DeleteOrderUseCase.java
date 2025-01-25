package com.santanna.serviceorder.application.usecase;

import com.santanna.serviceorder.application.usecase.exception.NotFoundException;
import com.santanna.serviceorder.application.utils.LoggerUtils;
import com.santanna.serviceorder.domain.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class DeleteOrderUseCase {


    private final LoggerUtils loggerUtils;
    private final OrderRepository orderRepository;

    public DeleteOrderUseCase(LoggerUtils loggerUtils, OrderRepository orderRepository) {
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
