package com.santanna.serviceorder.infra.repository;

import com.santanna.serviceorder.application.mapper.OrderMapper;
import com.santanna.serviceorder.domain.common.PaginatedResult;
import com.santanna.serviceorder.domain.model.Order;
import com.santanna.serviceorder.domain.repository.OrderRepository;
import com.santanna.serviceorder.infra.persistence.SpringDataOrderRepository;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.stream.Collectors;

public class OrderRepositoryImpl implements OrderRepository {

    private final SpringDataOrderRepository springDataOrderRepository;
    private final OrderMapper orderMapper;

    public OrderRepositoryImpl(SpringDataOrderRepository springDataOrderRepository, OrderMapper orderMapper) {
        this.springDataOrderRepository = springDataOrderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return springDataOrderRepository.findByOrderNumber(orderNumber).map(orderMapper::toDomain);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return springDataOrderRepository.findById(id).map(orderMapper::toDomain);
    }

    @Override
    public PaginatedResult<Order> findAll(int page, int size) {
        var entityPage = springDataOrderRepository.findAll(PageRequest.of(page, size));

        return new PaginatedResult<>(
                entityPage.getContent().stream()
                        .map(orderMapper::toDomain)
                        .collect(Collectors.toList()),
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements()
        );

    }

    @Override
    public Order save(Order order) {
        var entity = orderMapper.toEntity(order);
        var savedEntity = springDataOrderRepository.save(entity);
        return orderMapper.toDomain(savedEntity);

    }

    @Override
    public void deleteById(Long id) {
        springDataOrderRepository.deleteById(id);
    }
}
