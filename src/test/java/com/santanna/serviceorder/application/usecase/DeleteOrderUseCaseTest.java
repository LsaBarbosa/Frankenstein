package com.santanna.serviceorder.application.usecase;

import com.santanna.serviceorder.application.usecase.exception.BusinessException;
import com.santanna.serviceorder.application.usecase.exception.NotFoundException;
import com.santanna.serviceorder.application.utils.LoggerUtils;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class DeleteOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private LoggerUtils loggerUtils;

    @InjectMocks
    private DeleteOrderUseCase deleteOrderUseCase;

    private final Long ORDER_ID = 1L;

    @Test
    @DisplayName("Should delete order successfully")
    void shouldDeleteOrderSuccessfully() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(new Order()));
        assertDoesNotThrow(() -> deleteOrderUseCase.execute(ORDER_ID));

        verify(orderRepository).deleteById(ORDER_ID);
        verify(loggerUtils).logInfo(
                eq(DeleteOrderUseCase.class),
                eq("Order with ID {} deleted successfully"),
                eq(ORDER_ID)
        );

        verify(orderRepository).deleteById(ORDER_ID);

        verify(loggerUtils).logInfo(
                eq(DeleteOrderUseCase.class),
                eq("Order with ID {} deleted successfully"),
                eq(ORDER_ID)
        );
    }

    @Test
    @DisplayName("Shoul Throw NotFound Exception When Id Not Founded")
    void shouldThrowNotFoundExceptionWhenIdNotFounded() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());
        var notFoundException = assertThrows(
                NotFoundException.class,
                () -> deleteOrderUseCase.execute(ORDER_ID)
        );

        assertEquals("Order not found with ID: " + ORDER_ID, notFoundException.getMessage());

        // Verificamos se os logs adequados foram chamados
        verify(loggerUtils).logInfo(
                eq(DeleteOrderUseCase.class),
                eq("Attempting to delete order with ID: {}"),
                eq(ORDER_ID)
        );
        verify(loggerUtils).logWarn(
                eq(DeleteOrderUseCase.class),
                eq("Order with ID {} not found"),
                eq(ORDER_ID)
        );

        // Garantimos que não seja feita nenhuma chamada ao deleteById
        verify(orderRepository, never()).deleteById(ORDER_ID);
    }

    @Test
    @DisplayName("should Throw BusinessException When Any Error Happens During Deletion")
    void shouldThrowBusinessExceptionWhenAnyErrorHappensDuringDeletion() {

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(new Order()));

        doThrow(new RuntimeException("Error")).when(orderRepository).deleteById(ORDER_ID);

         var businessException = assertThrows(
                BusinessException.class,
                () -> deleteOrderUseCase.execute(ORDER_ID)
        );


        assertEquals("Order cannot delete: " + ORDER_ID, businessException.getMessage());

         verify(loggerUtils).logInfo(
                eq(DeleteOrderUseCase.class),
                eq("Attempting to delete order with ID: {}"),
                eq(ORDER_ID)
        );

         verify(orderRepository).deleteById(ORDER_ID);
    }

}