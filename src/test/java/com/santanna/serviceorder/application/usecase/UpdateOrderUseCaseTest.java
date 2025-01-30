package com.santanna.serviceorder.application.usecase;


import com.santanna.serviceorder.application.dto.OrderRequestDto;
import com.santanna.serviceorder.application.usecase.exception.NotFoundException;
import com.santanna.serviceorder.application.utils.LoggerUtils;
import com.santanna.serviceorder.domain.model.Order;
import com.santanna.serviceorder.domain.model.OrderConverter;
import com.santanna.serviceorder.domain.model.OrderStatus;
import com.santanna.serviceorder.domain.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateOrderUseCaseTest {

    public static final String ID = "1L";
    @InjectMocks
    private UpdateOrderUseCase updateOrderUseCase;
    @Mock
    private LoggerUtils loggerUtils;
    @Mock
    OrderRepository orderRepository;
    private OrderRequestDto requestDto;
    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order(ID, "123", "Product A", 2, BigDecimal.valueOf(200.0)
                , OrderStatus.PROCESSED, LocalDateTime.now());
    }

    @Test
    @DisplayName("Shoul Throw NotFound Exception When Id Not Founded")
    public void shouldThrowNotFoundExceptionWhenIdNotFounded() {
        when(orderRepository.findById(ID)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> {
            updateOrderUseCase.execute(ID, OrderStatus.PROCESSED);
        });

        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }


    @Test
    @DisplayName("should Convert UpdateOrder To ResponseDto Correctly")
    public void shouldConvertUpdateOrderToResponseDtoCorrectly() {
        when(orderRepository.findById(ID)).thenReturn(Optional.of(order));

        order = new Order(ID, "123", "Product A", 2, BigDecimal.valueOf(200.0)
                , OrderStatus.DELIVERED, LocalDateTime.now());

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        var responseDto = updateOrderUseCase.execute(ID, OrderStatus.DELIVERED);
        var responseExpected = OrderConverter.toDto(order);

        assertEquals(responseExpected, responseDto);
    }

    @Test
    @DisplayName("should UpdateOrder Correctly")
    public void shouldUpdateOrderCorrectly() {
        when(orderRepository.findById(ID)).thenReturn(Optional.of(order));

        order = new Order(ID, "123", "Product A", 2, BigDecimal.valueOf(200.0)
                , OrderStatus.DELIVERED, LocalDateTime.now());

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        var responseDto = updateOrderUseCase.execute(ID, OrderStatus.DELIVERED);

        assertNotNull(responseDto);
        assertNotNull(responseDto.id());
        assertEquals(OrderStatus.DELIVERED, responseDto.status());
        assertNotNull(responseDto.createdAt());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(loggerUtils).logInfo(
                eq(UpdateOrderUseCase.class),
                eq("Updating order status. ID: {}, New Status: {}"),
                eq(ID),
                eq(OrderStatus.DELIVERED)
        );

        verify(loggerUtils).logInfo(
                eq(UpdateOrderUseCase.class),
                eq("Order status updated successfully. ID: {}, New Status: {}"),
                eq(ID),
                eq(OrderStatus.DELIVERED)
        );
    }

}