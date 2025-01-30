package com.santanna.serviceorder.infrastructure.persistence;

import com.santanna.serviceorder.infrastructure.entity.OrderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataOrderRepository extends MongoRepository<OrderEntity, String> {
    Optional<OrderEntity> findByOrderNumber(String orderNumber);
}
