package com.santanna.serviceorder.application.usecase;


import com.santanna.serviceorder.application.dto.OrderRequestDto;
import com.santanna.serviceorder.application.usecase.exception.BusinessException;
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
public class CreateOrderUseCaseTest {

    @InjectMocks
    private CreateOrderUseCase createOrderUseCase;
    @Mock
    private LoggerUtils loggerUtils;
    @Mock
    private OrderRepository orderRepository;
    private OrderRequestDto requestDto;
    private Order order;

    @BeforeEach
    void setUp() {
        requestDto = new OrderRequestDto("123", "Product A", 2, BigDecimal.valueOf(100.0));

        order = new Order("1", "123", "Product A", 2, BigDecimal.valueOf(200.0)
                , OrderStatus.PROCESSED, LocalDateTime.now());
    }

    @Test
    @DisplayName("Should Throw Business Exception When OrderNumber Exists")
    public void shouldThrowBusinessExceptionWhenOrderNumberExists() {
        when(orderRepository.findByOrderNumber("123")).thenReturn(Optional.of(order));

        var exception = assertThrows(BusinessException.class, () -> createOrderUseCase.execute(requestDto));

        assertEquals("There is already an order with this number.", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("should CreateOrder With Correct Values When OrderNumber Is Unique")
    public void shouldCreateOrderWithCorrectValuesWhenOrderNumberIsUnique() {
        when(orderRepository.findByOrderNumber("123")).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        var responseDto = createOrderUseCase.execute(requestDto);

        assertNotNull(responseDto.id());
        assertEquals(requestDto.orderNumber(), responseDto.orderNumber());
        assertEquals(requestDto.productName(), responseDto.productName());
        assertEquals(requestDto.quantity(), responseDto.quantity());
        assertEquals(
                requestDto.unitPrice().multiply(BigDecimal.valueOf(requestDto.quantity())),
                responseDto.totalValue()
        );
        assertEquals(OrderStatus.PROCESSED, responseDto.status());
        assertNotNull(responseDto.createdAt());
        verify(orderRepository, times(1)).findByOrderNumber("123");
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(loggerUtils, times(1)).logInfo(eq(CreateOrderUseCase.class), anyString(), any());
    }

    @Test
    @DisplayName("should Convert SavedOrder To ResponseDto Correctly")
    void shouldConvertSavedOrderToResponseDtoCorrectly() {
        when(orderRepository.findByOrderNumber("123")).thenReturn(Optional.empty());
        when(orderRepository.save(any())).thenReturn(order);

        var responseDto = createOrderUseCase.execute(requestDto);

        var responseExpected = OrderConverter.toDto(order);
        assertEquals(responseExpected, responseDto);

    }
}