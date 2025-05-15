package com.ylli.shared.base;

public interface BaseService<D, K> {
    D getById(K id);

    D create(D dto);

    D update(K id, D dto);

    D delete(K id);
}
