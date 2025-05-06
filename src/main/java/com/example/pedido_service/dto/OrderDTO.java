package com.example.pedido_service.dto;

import com.example.pedido_service.enums.OrderStatus;
import com.example.pedido_service.enums.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;

    private LocalDateTime dateCreated;

    private OrderStatus status;

    private List<OrderedItemDTO> products;

    private PaymentMethod paymentMethod;

    private BigDecimal totalPrice;

    @Min(value = 0, message = "Discount must be 0 (zero) or greater")
    private Integer discount;

    @Min(value = 0, message = "Shipping Fee must be 0 (zero) or greater")
    private BigDecimal shippingFee;
}
