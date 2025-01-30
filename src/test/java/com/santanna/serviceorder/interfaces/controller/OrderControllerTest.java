package com.santanna.serviceorder.interfaces.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santanna.serviceorder.application.dto.OrderRequestDto;
import com.santanna.serviceorder.application.dto.OrderResponseDto;
import com.santanna.serviceorder.application.usecase.CreateOrderUseCase;
import com.santanna.serviceorder.application.usecase.DeleteOrderUseCase;
import com.santanna.serviceorder.application.usecase.GetOrderUseCase;
import com.santanna.serviceorder.application.usecase.UpdateOrderUseCase;
import com.santanna.serviceorder.application.usecase.exception.BusinessException;
import com.santanna.serviceorder.application.usecase.exception.NotFoundException;
import com.santanna.serviceorder.application.utils.LoggerUtils;
import com.santanna.serviceorder.domain.common.PaginatedResult;
import com.santanna.serviceorder.domain.model.OrderStatus;
import com.santanna.serviceorder.domain.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;
    @Mock
    private GetOrderUseCase getOrderUseCase;
    @Mock
    private CreateOrderUseCase createOrderUseCase;
    @Mock
    private UpdateOrderUseCase updateOrderUseCase;
    @Mock
    private DeleteOrderUseCase deleteOrderUseCase;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private LoggerUtils loggerUtils;

    private MockMvc mockMvc;
    private final String ORDER_ID = "1L";
    private final OrderResponseDto mockResponse = createMockResponse();


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    @DisplayName(" Should be able to creatre a new order")
    public void shouldBeAbleToCreateNewOrder() throws Exception {
        var requestDto = new OrderRequestDto(
                "ORD123",
                "Product A",
                2,
                BigDecimal.valueOf(200.0));

        when(createOrderUseCase.execute(requestDto)).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber").value("ORD123"))
                .andExpect(jsonPath("$.productName").value("Product A"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.totalValue").value(200.0));
    }

    @Test
    @DisplayName(" Should be able to updateStatus orders")
    public void shouldBeAbleToUpdateOrderStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/orders/{id}/status", ORDER_ID)
                        .param("newStatus","DELIVERED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(updateOrderUseCase).execute(eq(ORDER_ID), eq(OrderStatus.DELIVERED));

        verify(loggerUtils).logInfo(
                eq(OrderController.class),
                eq("Order with ID {} not found"),
                eq(ORDER_ID)
        );
    }

    @Test
    @DisplayName(" Should be able to get a order")
    public void shouldBeAbleToGetOrder() throws Exception {
        when(getOrderUseCase.getById(eq(ORDER_ID))).thenReturn(mockResponse);
        mockMvc.perform(get("/orders/{id}", ORDER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ORDER_ID))
                .andExpect(jsonPath("$.orderNumber").value("ORD123"))
                .andExpect(jsonPath("$.productName").value("Product A"))
                .andExpect(jsonPath("$.status").value("PROCESSED"));

        verify(loggerUtils).logInfo(eq(OrderController.class),
                eq("Received request to fetch order by ID: {}"), eq(ORDER_ID));
        verify(loggerUtils).logInfo(eq(OrderController.class),
                eq("Order retrieved successfully. ID: {}"), eq(ORDER_ID));
    }

    @Test
    @DisplayName(" Should be able to getAll orders")
    public void shouldBeAbleToGetAllOrders() throws Exception {

        List<OrderResponseDto> mockOrders = List.of(
                new OrderResponseDto("1L", "ORD123", "Product A",
                        2, BigDecimal.valueOf(200.0), OrderStatus.PROCESSED, LocalDateTime.now()),
                new OrderResponseDto("2L", "ORD456", "Product B",
                        1, BigDecimal.valueOf(150.0), OrderStatus.DELIVERED, LocalDateTime.now())
        );

        PaginatedResult<OrderResponseDto> mockResult = new PaginatedResult<>
                (mockOrders, 0, 10, 2L);

        when(getOrderUseCase.getAllOrders(anyInt(), anyInt())).thenReturn(mockResult);

        // Execução e verificação
        mockMvc.perform(get("/orders")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].orderNumber").value("ORD123"))
                .andExpect(jsonPath("$.content[1].orderNumber").value("ORD456"))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(loggerUtils).logInfo(eq(OrderController.class), eq("Listing orders - Page: {}, Size: {}"), eq(0), eq(10));
    }

    @Test
    @DisplayName(" Should be able to delete order")
    public void shouldBeAbleToDeleteOrder() throws Exception {
       mockMvc.perform(MockMvcRequestBuilders.delete("/orders/{id}", ORDER_ID))
               .andExpect(status().isNoContent());
       verify(deleteOrderUseCase).execute(ORDER_ID);

        verify(loggerUtils).logInfo(
                eq(OrderController.class),
                eq("Order with ID {} not found"),
                eq(ORDER_ID)
        );
    }

    @Test
    @DisplayName(" Should Throw NotFoundException When OrderNumber Exists")
    public void shouldThrowNotFoundExceptionWhenOrderNumberExists() throws Exception {
        var requestDto = new OrderRequestDto(
                "ORD123",
                "Product A",
                2,
                BigDecimal.valueOf(200.0));

        when(createOrderUseCase.execute(eq(requestDto))).thenThrow
                (new BusinessException("There is already an order with this number."));

        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(requestDto)))
                .andExpect(status().isUnprocessableEntity());

        // Verifica se o log foi registrado corretamente
        verify(loggerUtils).logInfo(
                eq(OrderController.class),
                eq("Receiving request to create order: {}"),
                eq("ORD123")
        );
    }

    @Test
    @DisplayName(" Should Throw NotFoundException When Id Exists")
    public void shouldThrowNotFoundExceptionWhenIdExists() throws Exception {
        when(getOrderUseCase.getById(eq(ORDER_ID))).thenThrow(new NotFoundException("Order not found"));

        mockMvc.perform(get("/orders/{id}", ORDER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(loggerUtils).logInfo(eq(OrderController.class),
                eq("Received request to fetch order by ID: {}"), eq(ORDER_ID));
    }

    @Test
    @DisplayName("Should throw NotFoundException when updating non-existent order")
    public void shouldThrowNotFoundExceptionWhenUpdatingNonExistentOrder() throws Exception {
        String invalidId = "9999L";

        when(updateOrderUseCase.execute(eq(invalidId),any())).thenThrow(new NotFoundException("Order not found"));
        mockMvc.perform(MockMvcRequestBuilders.patch("/orders/{id}/status", invalidId)
                .param("newStatus","DELIVERED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(loggerUtils).logInfo(
                eq(OrderController.class),
                eq("Order with ID {} not found"),
                eq(invalidId)
        );
    }

    @Test
    @DisplayName("should Throw NotFoundException When Deleting Non Existent Order")
    public void shouldThrowNotFoundExceptionWhenDeletingNonExistentOrder() throws Exception {
        String invalidId = "9999L";

        doThrow(new NotFoundException("Order not found with ID: " + invalidId))
                .when(deleteOrderUseCase).execute(invalidId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/orders/{id}", invalidId))
                .andExpect(status().isNotFound()); // Status 404

        verify(loggerUtils).logInfo(
                eq(OrderController.class),
                eq("Order with ID {} not found"),
                eq(invalidId)
        );
    }

    private static String objectToJson(Object object) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        final String json = mapper.writeValueAsString(object);
        return json;
    }

    private OrderResponseDto createMockResponse() {
        return new OrderResponseDto(
                ORDER_ID,
                "ORD123",
                "Product A",
                2,
                BigDecimal.valueOf(200.0),
                OrderStatus.PROCESSED,
                LocalDateTime.now()
        );
    }
}
