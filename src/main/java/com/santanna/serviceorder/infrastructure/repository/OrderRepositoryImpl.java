package com.santanna.serviceorder.infrastructure.repository;

import com.santanna.serviceorder.domain.builder.OrderDirector;
import com.santanna.serviceorder.domain.builder.objectbuild.CreateOrderBuilder;
import com.santanna.serviceorder.domain.common.PaginatedResult;
import com.santanna.serviceorder.domain.model.Order;
import com.santanna.serviceorder.domain.repository.OrderRepository;
import com.santanna.serviceorder.infrastructure.entity.OrderEntity;
import com.santanna.serviceorder.infrastructure.exception.DatabaseException;
import com.santanna.serviceorder.infrastructure.persistence.SpringDataOrderRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final SpringDataOrderRepository springDataOrderRepository;


    public OrderRepositoryImpl(SpringDataOrderRepository springDataOrderRepository ) {
        this.springDataOrderRepository = springDataOrderRepository;

    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        try {
            return springDataOrderRepository.findByOrderNumber(orderNumber)
                    .map(this::toDomain);
        } catch (Exception e) {
            throw new DatabaseException("Error finding order by order number: " + orderNumber, e);
        }
    }

    @Override
    public Optional<Order> findById(String id) {
        try {
            return springDataOrderRepository.findById(id)
                    .map(this::toDomain);
        } catch (Exception e) {
            throw new DatabaseException("Error finding order by ID: " + id, e);
        }
    }

    @Override
    public PaginatedResult<Order> findAll(int page, int size) {
        try {
            var entityPage = springDataOrderRepository.findAll(PageRequest.of(page, size));
            return new PaginatedResult<>(
                    entityPage.getContent().stream()
                            .map(this::toDomain)
                            .collect(Collectors.toList()),
                    entityPage.getNumber(),
                    entityPage.getSize(),
                    entityPage.getTotalElements()
            );
        } catch (Exception e) {
            throw new DatabaseException("Error fetching paginated orders", e);
        }
    }

    @Override
    public Order save(Order order) {
        try {
            OrderEntity entity = this.toEntity(order);
            OrderEntity savedEntity = springDataOrderRepository.save(entity);
            return toDomain(savedEntity);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Order already exists with number: " + order.getOrderNumber(), e);
        } catch (Exception e) {
            throw new DatabaseException("Error saving order", e);
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            springDataOrderRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new DatabaseException("Order with ID " + id + " not found.", e);
        } catch (Exception e) {
            throw new DatabaseException("Error deleting order with ID: " + id, e);
        }
    }


    CreateOrderBuilder createOrderBuilder = new CreateOrderBuilder();
    OrderDirector director = new OrderDirector(createOrderBuilder);

    private Order toDomain(OrderEntity entity) {
        return new Order(
                entity.getId(),
                entity.getOrderNumber(),
                entity.getProductName(),
                entity.getQuantity(),
                entity.getTotalValue(),
                entity.getOrderStatus(),
                entity.getCreatedAt()
        );
    }

    private OrderEntity toEntity(Order order) {
        return new OrderEntity(
                order.getId(),
                order.getOrderNumber(),
                order.getProductName(),
                order.getQuantity(),
                order.getTotalValue(),
                order.getOrderStatus(),
                order.getCreatedAt()
        );
    }

}
