package com.example.pedido_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderedItemDTO {

    @NotNull
    private Long productId;

    private String productDescription;

    private BigDecimal productPrice;

    @Positive(message = "Quantity ordered must be greater than 0 (zero)")
    private Integer quantity;
}
