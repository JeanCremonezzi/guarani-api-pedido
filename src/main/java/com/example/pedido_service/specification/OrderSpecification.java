package com.example.pedido_service.specification;

import com.example.pedido_service.enums.OrderStatus;
import com.example.pedido_service.model.Order;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderSpecification {

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Order> dateCreatedBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start != null && end != null)
                return cb.between(root.get("dateCreated"), start, end);
            if (start != null)
                return cb.greaterThanOrEqualTo(root.get("dateCreated"), start);
            if (end != null)
                return cb.lessThanOrEqualTo(root.get("dateCreated"), end);
            return null;
        };
    }

    public static Specification<Order> totalPriceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min != null && max != null)
                return cb.between(root.get("totalPrice"), min, max);
            if (min != null)
                return cb.greaterThanOrEqualTo(root.get("totalPrice"), min);
            if (max != null)
                return cb.lessThanOrEqualTo(root.get("totalPrice"), max);
            return null;
        };
    }
}

