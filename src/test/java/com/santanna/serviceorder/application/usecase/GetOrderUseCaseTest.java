package com.santanna.serviceorder.application.usecase;

import com.santanna.serviceorder.application.dto.OrderResponseDto;
import com.santanna.serviceorder.application.usecase.exception.NotFoundException;
import com.santanna.serviceorder.application.utils.LoggerUtils;
import com.santanna.serviceorder.domain.common.PaginatedResult;
import com.santanna.serviceorder.domain.model.Order;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOrderUseCaseTest {
    @Mock
    private LoggerUtils loggerUtils;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private GetOrderUseCase getOrderUseCase;

    private Order order;
    @BeforeEach
    void setUp() {


        order = new Order(1L, "123", "Product A", 2, BigDecimal.valueOf(200.0)
                , OrderStatus.PROCESSED, LocalDateTime.now());

    }

    @Test
    @DisplayName("should Return OrderResponseDto When Order Found")
    void shouldReturnOrderResponseDtoWhenOrderFound() {

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        var responseDto = getOrderUseCase.getById(1L);

        assertNotNull(responseDto);
        assertNotNull(responseDto.id());
        assertEquals(order.getOrderNumber(), responseDto.orderNumber());
        assertEquals(order.getProductName(), responseDto.productName());
        assertEquals(order.getQuantity(), responseDto.quantity());
        assertEquals(order.getTotalValue(), responseDto.totalValue()
        );
        assertEquals(OrderStatus.PROCESSED, responseDto.status());
        assertNotNull(responseDto.createdAt());

        verify(loggerUtils).logInfo(
                eq(GetOrderUseCase.class),
                eq("Fetching order by ID: {}"),
                eq(1L)
        );
        verify(loggerUtils).logInfo(
                eq(GetOrderUseCase.class),
                eq("Order found. ID: {}"),
                eq(1L)
        );

        verify(orderRepository).findById(1L);
    }

    @Test
    @DisplayName("should Throw NotFoundException When Order NotFound")
    void shouldThrowNotFoundExceptionWhenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> getOrderUseCase.getById(99L)
        );

        assertEquals("Order not found", notFoundException.getMessage());

        verify(loggerUtils).logInfo(
                eq(GetOrderUseCase.class),
                eq("Fetching order by ID: {}"),
                eq(99L)
        );

        verify(orderRepository).findById(99L);
    }

    @Test
    @DisplayName("should Return All Orders Paginated")
    void shouldReturnAllOrdersPaginated() {
        Order order1 = new Order();
       var order2 = new Order(2L, "124", "Product B", 1, BigDecimal.valueOf(100.0)
                , OrderStatus.PROCESSED, LocalDateTime.now());

        List<Order> orderList = Arrays.asList(order1, order2);

        PaginatedResult<Order> paginatedOrders = new PaginatedResult<>(
                orderList, 0, 2, 2L
        );

        when(orderRepository.findAll(0, 2)).thenReturn(paginatedOrders);

        PaginatedResult<OrderResponseDto> result = getOrderUseCase.getAllOrders(0, 2);

        assertNotNull(result);


        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getPageNumber());
        assertEquals(2, result.getPageSize());
        assertEquals(2L, result.getTotalElements());

        verify(loggerUtils).logInfo(
                eq(GetOrderUseCase.class),
                eq("Fetching all orders with pagination")
        );
        verify(loggerUtils).logInfo(
                eq(GetOrderUseCase.class),
                eq("Retrieved {} orders successfully")
        );
        verify(orderRepository).findAll(0, 2);
    }
}