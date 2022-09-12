package com.food.ordering.system.domain.entity;

import java.util.Objects;

/**
 * The DDD there are multiple Entities each of them will extend this class.
 * Each entity will have a unique Id.
 * @param <T>
 */
public abstract class BaseEntity<T> {
    private T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity<?> that = (BaseEntity<?>) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
