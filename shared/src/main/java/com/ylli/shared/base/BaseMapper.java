package com.ylli.shared.base;

import java.util.List;

public interface BaseMapper<E, D> {
    D toDto(E entity);

    E toEntity(D dto);

    default List<D> toDtoList(List<E> entities){
        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    default List<E> toEntityList(List<D> dtos){
        return dtos.stream()
                .map(this::toEntity)
                .toList();
    }

}
