package com.santanna.serviceorder.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrderRequestDto(@Schema(description = "Número único do pedido", example = "ORD12345")
                              @NotBlank(message = "O número do pedido é obrigatório.")
                              String orderNumber,

                              @Schema(description = "Nome do produto", example = "Produto Exemplo")
                              @NotBlank(message = "O nome do produto é obrigatório.")
                              String productName,

                              @Schema(description = "Quantidade de itens", example = "10")
                              @NotNull(message = "A quantidade é obrigatória.")
                              @Min(value = 1, message = "A quantidade deve ser no mínimo 1.")
                              Integer quantity,

                              @Schema(description = "Preço unitário do produto", example = "100.00")
                              @NotNull(message = "O preço unitário é obrigatório.")
                              @DecimalMin(value = "0.01", message = "O preço unitário deve ser maior que zero.")
                              BigDecimal unitPrice
) {
}
