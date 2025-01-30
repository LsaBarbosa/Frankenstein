package com.santanna.serviceorder.application.dto;

import com.santanna.serviceorder.domain.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record OrderResponseDto (
    @Schema(description = "ID do pedido", example = "1")
    String id,

    @Schema(description = "Número do pedido", example = "ORD12345")
    String orderNumber,

    @Schema(description = "Nome do produto", example = "Produto Exemplo")
    String productName,

    @Schema(description = "Quantidade do produto", example = "10")
    Integer quantity,

    @Schema(description = "Valor total do pedido", example = "1000.00")
    BigDecimal totalValue,

    @Schema(description = "Status do pedido", example = "PROCESSED")
    OrderStatus status,

   // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Data de criação do pedido", example = "2024-01-01T12:00:00")
    LocalDateTime createdAt
) {}