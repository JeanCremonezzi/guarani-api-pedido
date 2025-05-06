package com.example.pedido_service.service;

import com.example.pedido_service.dto.*;
import com.example.pedido_service.enums.OrderStatus;
import com.example.pedido_service.model.Order;
import com.example.pedido_service.model.OrderedItem;
import com.example.pedido_service.model.Product;
import com.example.pedido_service.repository.OrderRepository;
import com.example.pedido_service.repository.ProductRepository;
import com.example.pedido_service.specification.OrderSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public OrderDTO createOrder(CreateOrderDTO orderDTO) throws RuntimeException {
        Order order = convertToEntity(orderDTO);

        order.setStatus(OrderStatus.PENDING);
        order.setDateCreated(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        return convertToDto(savedOrder);
    }

    public List<OrderDTO> findAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public OrderDTO findOrderById(Long id) throws RuntimeException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        return convertToDto(order);
    }

    public OrderDTO cancelOrderById(Long id) throws RuntimeException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be cancelled.");
        }

        for (OrderedItem orderedItem : order.getProducts()) {
            Product product = orderedItem.getProduct();
            product.setQuantityStock(product.getQuantityStock() + orderedItem.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return convertToDto(order);
    }

    public OrderDTO updateOrder(Long id, UpdateOrderDTO orderDTO) throws RuntimeException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update CANCELLED orders.");
        }

        for (OrderedItem oldItem : order.getProducts()) {
            Product product = oldItem.getProduct();
            product.setQuantityStock(product.getQuantityStock() + oldItem.getQuantity());
            productRepository.save(product);
        }

        order.setDiscount(orderDTO.getDiscount());
        order.setShippingFee(orderDTO.getShippingFee());
        order.setStatus(orderDTO.getStatus());

        List<OrderedItem> updatedItems = orderDTO.getProducts().stream().map(itemDTO -> {
            Product product = checkProduct(itemDTO);

            OrderedItem item = new OrderedItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitaryPrice(product.getPrice());

            return item;
        }).collect(Collectors.toList());

        order.getProducts().clear();
        order.getProducts().addAll(updatedItems);

        BigDecimal totalPrice = calculateTotalPrice(updatedItems, order.getDiscount(), order.getShippingFee());
        order.setTotalPrice(totalPrice);

        Order updatedOrder = orderRepository.save(order);
        return convertToDto(updatedOrder);
    }

    public List<OrderDTO> findFilteredOrders(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate, BigDecimal minPrice, BigDecimal maxPrice) {
        Specification<Order> spec = Specification
                .where(OrderSpecification.hasStatus(status))
                .and(OrderSpecification.dateCreatedBetween(startDate, endDate))
                .and(OrderSpecification.totalPriceBetween(minPrice, maxPrice));

        return orderRepository.findAll(spec).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private BigDecimal calculateTotalPrice(List<OrderedItem> orderedItems, int discount, BigDecimal shippingFee) {
        BigDecimal totalPrice = orderedItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountPercent = BigDecimal.valueOf(discount)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal discountAmount = totalPrice.multiply(discountPercent);
        totalPrice = totalPrice.subtract(discountAmount);

        totalPrice = totalPrice.add(shippingFee);

        return totalPrice;
    }

    private Order convertToEntity(CreateOrderDTO orderDTO) {
        Order order = new Order();
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setDiscount(orderDTO.getDiscount());
        order.setShippingFee(orderDTO.getShippingFee());

        List<OrderedItem> items = orderDTO.getProducts().stream().map(itemDTO -> {
            OrderedItem item = new OrderedItem();
            item.setOrder(order);

            Product product = checkProduct(itemDTO);

            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitaryPrice(product.getPrice());

            return item;
        }).collect(Collectors.toList());
        order.setProducts(items);

        BigDecimal totalPrice = calculateTotalPrice(order.getProducts(), order.getDiscount(), order.getShippingFee());

        order.setTotalPrice(totalPrice);

        return order;
    }

    private Product checkProduct(CreateOrderedItemDTO itemDTO) {
        Product product = productRepository.findById(itemDTO.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + itemDTO.getProductId()));

        if (product.isDisabled()) {
            throw new IllegalStateException("Product is inactive and cannot be ordered: " + itemDTO.getProductId());
        }

        if (product.getQuantityStock() < itemDTO.getQuantity()) {
            throw new IllegalStateException("Insufficient stock for product" + product.getDescription());
        }

        product.setQuantityStock(product.getQuantityStock() - itemDTO.getQuantity());
        return product;
    }

    private OrderDTO convertToDto(Order order) {
        List<OrderedItemDTO> productDTOs = order.getProducts().stream().map(item -> new OrderedItemDTO(
                item.getProduct().getId(),
                item.getProduct().getDescription(),
                item.getProduct().getPrice(),
                item.getQuantity()
        )).toList();

        return new OrderDTO(
                order.getId(),
                order.getDateCreated(),
                order.getStatus(),
                productDTOs,
                order.getPaymentMethod(),
                order.getTotalPrice(),
                order.getDiscount(),
                order.getShippingFee()
        );
    }
}
