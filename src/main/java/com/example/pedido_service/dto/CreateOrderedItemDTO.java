package com.example.pedido_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderedItemDTO {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;
}
