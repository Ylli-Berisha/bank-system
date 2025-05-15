package com.ylli.shared.base;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

public abstract class BaseController<D, K, S extends BaseService<D, K>> {

    protected final S service;

    protected BaseController(S service) {
        this.service = service;
    }

    @GetMapping("get/{id}")
    public ResponseEntity<D> getById(@PathVariable K id) {
        validateId(id);
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("create")
    public ResponseEntity<D> create(@Valid @RequestBody D dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("update/{id}")
    public ResponseEntity<D> update(@PathVariable K id, @Valid @RequestBody D dto) {
        validateId(id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<D> delete(@PathVariable K id) {
        validateId(id);
        return ResponseEntity.ok(service.delete(id));
    }

    protected void validateId(K id) {
        if (id == null || (id instanceof String && ((String) id).isEmpty())) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
    }
}