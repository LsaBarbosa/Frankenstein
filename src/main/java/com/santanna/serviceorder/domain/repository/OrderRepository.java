package com.santanna.serviceorder.domain.repository;

import com.santanna.serviceorder.domain.common.PaginatedResult;
import com.santanna.serviceorder.domain.model.Order;

import java.util.Optional;

public interface OrderRepository {

    Optional<Order> findByOrderNumber(String orderNumber);

    Optional<Order> findById(Long id);

    PaginatedResult<Order> findAll(int page, int size);

    Order save(Order order);

    void deleteById(Long id);
}
