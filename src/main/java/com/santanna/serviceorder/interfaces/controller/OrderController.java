package com.santanna.serviceorder.interfaces.controller;

import com.santanna.serviceorder.application.dto.OrderRequestDto;
import com.santanna.serviceorder.application.dto.OrderResponseDto;
import com.santanna.serviceorder.application.usecase.CreateOrderUseCase;
import com.santanna.serviceorder.application.usecase.DeleteOrderUseCase;
import com.santanna.serviceorder.application.usecase.GetOrderUseCase;
import com.santanna.serviceorder.application.usecase.UpdateOrderUseCase;
import com.santanna.serviceorder.application.utils.LoggerUtils;
import com.santanna.serviceorder.domain.common.PaginatedResult;
import com.santanna.serviceorder.domain.model.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Tag(name = "Order Controller", description = "Gerenciamento de pedidos")
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final CreateOrderUseCase createOrderUseCase;
    private final UpdateOrderUseCase updateOrderStatusUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final DeleteOrderUseCase deleteOrderUseCase;
    private final LoggerUtils loggerUtils;

    public OrderController(CreateOrderUseCase createOrderUseCase, UpdateOrderUseCase updateOrderStatusUseCase,
                           GetOrderUseCase getOrderUseCase, DeleteOrderUseCase deleteOrderUseCase,
                           LoggerUtils loggerUtils) {
        this.createOrderUseCase = createOrderUseCase;
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.deleteOrderUseCase = deleteOrderUseCase;
        this.loggerUtils = loggerUtils;
    }

    @Operation(summary = "Cria um novo pedido", description = "Cria um novo pedido")
    @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso")
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequestDto) {
        loggerUtils.logInfo(OrderController.class, "Received request to create an order: {}", orderRequestDto.getOrderNumber());

        OrderResponseDto createdOrder = createOrderUseCase.execute(orderRequestDto);

        loggerUtils.logInfo(OrderController.class, "Order successfully created with ID: {}", createdOrder.getId());
        return ResponseEntity.status(201).body(createdOrder);
    }

    @Operation(summary = "Atualizar status do pedido", description = "Atualiza o status de um pedido existente")
    @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso")
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateStatus(@PathVariable Long id, @RequestParam OrderStatus orderStatus) {
        loggerUtils.logInfo(OrderController.class, "Received request to update order status. ID: {}, New Status: {}", id, orderStatus);

        OrderResponseDto updatedOrder = updateOrderStatusUseCase.execute(id, orderStatus.name());

        loggerUtils.logInfo(OrderController.class, "Order status updated successfully. ID: {}, New Status: {}", id, updatedOrder.getStatus());
        return ResponseEntity.ok(updatedOrder);
    }

    @Operation(summary = "Listar pedidos", description = "Lista todos os pedidos com suporte a paginação")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos")
    @GetMapping
    public ResponseEntity<PaginatedResult<OrderResponseDto>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        loggerUtils.logInfo(OrderController.class, "Received request to retrieve all orders with pagination");

        PaginatedResult<OrderResponseDto> orders = getOrderUseCase.getAllOrders(page, size);

        loggerUtils.logInfo(OrderController.class, "Successfully retrieved {} orders", orders.getTotalElements());
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Buscar um pedido por ID", description = "Retorna os detalhes de um pedido específico")
    @ApiResponse(responseCode = "200", description = "Pedido encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        loggerUtils.logInfo(OrderController.class, "Received request to fetch order by ID: {}", id);

        OrderResponseDto order = getOrderUseCase.getById(id);

        loggerUtils.logInfo(OrderController.class, "Order retrieved successfully. ID: {}", order.getId());
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Excluir um pedido", description = "Remove um pedido pelo ID")
    @ApiResponse(responseCode = "204", description = "Pedido excluído com sucesso")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        loggerUtils.logWarn(OrderController.class, "Received request to delete order with ID: {}", id);

        deleteOrderUseCase.execute(id);

        loggerUtils.logInfo(OrderController.class, "Order with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}