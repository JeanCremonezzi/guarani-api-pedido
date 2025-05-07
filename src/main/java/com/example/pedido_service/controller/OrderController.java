package com.example.pedido_service.controller;

import com.example.pedido_service.dto.CreateOrderDTO;
import com.example.pedido_service.dto.OrderDTO;
import com.example.pedido_service.dto.UpdateOrderDTO;
import com.example.pedido_service.enums.OrderStatus;
import com.example.pedido_service.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Tag(name = "Orders", description = "Orders endpoints")
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
            summary = "Cria um novo Pedido",
            description = "Recebe os produtos, desconto, frete e método de pagamento do pedido."
    )
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody @Valid CreateOrderDTO orderDTO) throws RuntimeException {
        OrderDTO createdOrder = orderService.createOrder(orderDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @Operation(
            summary = "Busca todos os Pedidos registrados"
    )
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.findAllOrders();

        return ResponseEntity.ok(orders);
    }

    @Operation(
            summary = "Busca um Pedido",
            description = "Recebe o ID do Pedido buscado"
    )
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable("id") Long id) throws RuntimeException {
        OrderDTO order = orderService.findOrderById(id);

        return ResponseEntity.ok(order);
    }

    @Operation(
            summary = "Deleta um pedido",
            description = "Busca um Pedido pelo ID e altera o Status para CANCELLED"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<OrderDTO> cancelOrderById(@PathVariable("id") Long id) throws RuntimeException {
        OrderDTO updated = orderService.cancelOrderById(id);

        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Atualiza um Pedido",
            description = "Recebe o ID e altera os campos permitidos"
    )
    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable("id") Long id, @RequestBody @Valid UpdateOrderDTO orderDTO) throws RuntimeException {
        OrderDTO updated = orderService.updateOrder(id, orderDTO);

        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Busca Pedidos através de filtros",
            description = "Retorna todos os Pedidos que se encaixem nos filtros fornecidos"
    )
    @GetMapping("/search")
    public List<OrderDTO> searchOrders(
            @RequestParam(name = "status", required = false) OrderStatus status,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "minTotal", required = false) BigDecimal minTotal,
            @RequestParam(name = "maxTotal", required = false) BigDecimal maxTotal) {

        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;

        return orderService.findFilteredOrders(status, startDateTime, endDateTime, minTotal, maxTotal);
    }
}
