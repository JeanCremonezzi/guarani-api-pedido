package com.example.pedido_service.dto;

import com.example.pedido_service.enums.OrderStatus;
import com.example.pedido_service.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
