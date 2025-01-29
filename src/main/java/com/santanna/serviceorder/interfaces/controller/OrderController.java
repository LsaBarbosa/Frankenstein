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
import org.springframework.http.HttpStatus;
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

    @Operation(summary = "Cria um novo pedido", responses = {
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação")
    })
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequestDto) {
        loggerUtils.logInfo(OrderController.class, "Receiving request to create order: {}", orderRequestDto.orderNumber());
        OrderResponseDto createdOrder = createOrderUseCase.execute(orderRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }


    @Operation(summary = "Atualiza o status de um pedido", responses = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus newStatus) {
        loggerUtils.logInfo(OrderController.class, "Order with ID {} not found", id);
        updateOrderStatusUseCase.execute(id, newStatus);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Lista pedidos paginados", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<PaginatedResult<OrderResponseDto>> getOrders(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {
        loggerUtils.logInfo(OrderController.class, "Listing orders - Page: {}, Size: {}", page, size);
        PaginatedResult<OrderResponseDto> orders = getOrderUseCase.getAllOrders(page, size);
        return ResponseEntity.ok(orders);
    }


    @Operation(summary = "Buscar um pedido por ID", description = "Retorna os detalhes de um pedido específico")
    @ApiResponse(responseCode = "200", description = "Pedido encontrado")
    @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        loggerUtils.logInfo(OrderController.class, "Received request to fetch order by ID: {}", id);

        OrderResponseDto order = getOrderUseCase.getById(id);

        loggerUtils.logInfo(OrderController.class, "Order retrieved successfully. ID: {}", id);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Deleta um pedido pelo ID", responses = {
            @ApiResponse(responseCode = "204", description = "Pedido deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        loggerUtils.logInfo(OrderController.class, "Order with ID {} not found", id);
        deleteOrderUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}