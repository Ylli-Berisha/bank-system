package com.ylli.shared.base;
import com.ylli.shared.exceptions.ResourceNotFoundException;
import com.ylli.shared.exceptions.ValidationException;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class BaseServiceImpl<E extends BaseEntity<K>, D, K, R extends JpaRepository<E, K>, M extends BaseMapper<E, D>> implements BaseService<D, K> {

    protected final R repository;
    protected final M mapper;

    public BaseServiceImpl(R repository, M mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public D getById(K id) {
        if (id == null) {
            throw new ValidationException("ID cannot be null");
        }
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entity not found with id: " + id)));
    }

    @Override
    public D create(D dto) {
        if (dto == null) {
            throw new ValidationException("DTO cannot be null");
        }
        E entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public D update(K id, D dto) {
        if (id == null || dto == null) {
            throw new ValidationException(id == null ? "ID cannot be null" : "DTO cannot be null");
        }
        E entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entity not found with id: " + id));
        E updatedEntity = mapper.toEntity(dto);
        updatedEntity.setId(entity.getId());
        return mapper.toDto(repository.save(updatedEntity));
    }

    @Override
    public D delete(K id) {
        if (id == null) {
            throw new ValidationException("ID cannot be null");
        }
        E entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entity not found with id: " + id));
        repository.delete(entity);
        return mapper.toDto(entity);
    }
}