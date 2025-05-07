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
import org.springframework.security.access.prepost.PreAuthorize;
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
            description = "Usuários ADMIN, OPERADOR, ou CLIENTE podem criar um pedido"
    )
    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERADOR', 'SCOPE_CLIENTE')")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody @Valid CreateOrderDTO orderDTO) throws RuntimeException {
        OrderDTO createdOrder = orderService.createOrder(orderDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @Operation(
            summary = "Busca todos os Pedidos registrados",
            description = "Apenas usuários ADMIN ou OPERADOR podem acessar todos os pedidos registrados"
    )
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERADOR')")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.findAllOrders();

        return ResponseEntity.ok(orders);
    }

    @Operation(
            summary = "Busca um Pedido",
            description = "Usuários ADMIN, OPERADOR, ou CLIENTE podem acessar detalhes de um pedido específico"
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERADOR', 'SCOPE_CLIENTE')")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable("id") Long id) throws RuntimeException {
        OrderDTO order = orderService.findOrderById(id);

        return ResponseEntity.ok(order);
    }

    @Operation(
            summary = "Deleta um pedido",
            description = "Apenas ADMIN pode cancelar um pedido"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public ResponseEntity<OrderDTO> cancelOrderById(@PathVariable("id") Long id) throws RuntimeException {
        OrderDTO updated = orderService.cancelOrderById(id);

        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Atualiza um Pedido",
            description = "Apenas ADMIN ou OPERADOR podem atualizar um pedido"
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERADOR')")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable("id") Long id, @RequestBody @Valid UpdateOrderDTO orderDTO) throws RuntimeException {
        OrderDTO updated = orderService.updateOrder(id, orderDTO);

        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Busca Pedidos através de filtros",
            description = "Usuários ADMIN, OPERADOR, ou CLIENTE podem buscar pedidos filtrados por status, data, e valor"
    )
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERADOR', 'SCOPE_CLIENTE')")
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
