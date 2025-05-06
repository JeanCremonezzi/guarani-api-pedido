package com.example.pedido_service.dto;

import com.example.pedido_service.enums.PaymentMethod;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDTO {
    private List<CreateOrderedItemDTO> products;

    private PaymentMethod paymentMethod;

    @Min(value = 0, message = "Discount must be 0 (zero) or greater")
    private Integer discount;

    @Min(value = 0, message = "Shipping Fee must be 0 (zero) or greater")
    private BigDecimal shippingFee;
}
