package com.ylli.shared.base;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

public abstract class BaseController<D, K, S extends BaseService<D, K>> {

    protected final S service;

    protected BaseController(S service) {
        this.service = service;
    }

    @Operation(
            summary = "Get item by ID",
            description = "REST API to get an item by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved item",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("get/{id}")
    public ResponseEntity<D> getById(@PathVariable K id) {
        validateId(id);
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(
            summary = "Create item",
            description = "REST API to create an item"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created item",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("create")
    public ResponseEntity<D> create(@Valid @RequestBody D dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(
            summary = "Update item",
            description = "REST API to update an item"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated item",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid ID or input",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @PutMapping("update/{id}")
    public ResponseEntity<D> update(@PathVariable K id, @Valid @RequestBody D dto) {
        validateId(id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(
            summary = "Delete item by ID",
            description = "REST API to delete an item by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted item",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
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