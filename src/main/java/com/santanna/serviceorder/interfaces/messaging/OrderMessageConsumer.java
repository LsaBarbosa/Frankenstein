package com.santanna.serviceorder.interfaces.messaging;

import com.santanna.serviceorder.application.dto.OrderRequestDto;
import com.santanna.serviceorder.application.usecase.CreateOrderUseCase;
import com.santanna.serviceorder.application.utils.LoggerUtils;
import com.santanna.serviceorder.infrastructure.messaging.RabbitMqConfig;
import com.santanna.serviceorder.interfaces.exception.MessageValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class OrderMessageConsumer {
    private final CreateOrderUseCase orderService;
    private final Validator validator;
    private final LoggerUtils loggerUtils;

    public OrderMessageConsumer(CreateOrderUseCase orderService, Validator validator, LoggerUtils loggerUtils) {
        this.orderService = orderService;
        this.validator = validator;
        this.loggerUtils = loggerUtils;
    }

    @RabbitListener(queues = RabbitMqConfig.ORDER_QUEUE, concurrency = "3-10")
    public void receiveOrder(@Payload OrderRequestDto orderRequestDto) {
        try {
            loggerUtils.logInfo(OrderMessageConsumer.class, "Received new order message from queue. Order number: {}", orderRequestDto.orderNumber());

            Set<ConstraintViolation<OrderRequestDto>> violations = validator.validate(orderRequestDto);

            if (!violations.isEmpty()) {
                StringBuilder sb = new StringBuilder("Validation errors: ");
                for (ConstraintViolation<OrderRequestDto> violation : violations) {
                    sb.append(violation.getPropertyPath()).append(" ").append(violation.getMessage()).append("; ");
                }
                loggerUtils.logWarn(OrderMessageConsumer.class, "Validation failed for order number {}: {}", orderRequestDto.orderNumber(), sb.toString());

                throw new MessageValidationException(sb.toString());
            }

            orderService.execute(orderRequestDto);
            loggerUtils.logInfo(OrderMessageConsumer.class, "Order successfully processed. Order number: {}", orderRequestDto.orderNumber());

        } catch (Exception e) {
            loggerUtils.logWarn(OrderMessageConsumer.class, "Bad request error while processing order number {}: {}", orderRequestDto.orderNumber(), e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Erro crítico: " + e.getMessage());
        }
    }
}
