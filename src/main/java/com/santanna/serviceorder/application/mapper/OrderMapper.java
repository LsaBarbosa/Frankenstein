package com.santanna.serviceorder.application.mapper;

import com.santanna.serviceorder.application.dto.OrderRequestDto;
import com.santanna.serviceorder.application.dto.OrderResponseDto;
import com.santanna.serviceorder.domain.model.Order;
import com.santanna.serviceorder.infra.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {
   OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

   Order toDomain(OrderEntity orderEntity);
   OrderEntity toEntity(Order order);

   @Mapping(target = "toalValue", expression = "java(dto.getUnitPrice().multiply(java.math.BigDecimal.valueOf(dto.getQuantity())))")
   Order toDomain(OrderRequestDto requestDto);

   OrderResponseDto toDto(Order order);
}
