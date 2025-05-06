package com.example.pedido_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderedItemDTO {
    private Long productId;
    private String productDescription;
    private BigDecimal productPrice;
    private Integer quantity;
}
