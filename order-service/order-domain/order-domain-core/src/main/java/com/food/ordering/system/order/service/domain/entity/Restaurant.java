package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.Aggregate;
import com.food.ordering.system.domain.valueobject.RestaurantId;

import java.util.List;

public class Restaurant extends Aggregate<RestaurantId> {
    private final List<Product> products;
    private final boolean active;

    private Restaurant(Builder builder) {
        setId(builder.id);
        products = builder.products;
        active = builder.active;
    }

    public static Builder builder() {
        return new Builder();
    }


    public List<Product> getProducts() {
        return products;
    }

    public boolean isActive() {
        return active;
    }

    public static final class Builder {
        private RestaurantId id;
        private List<Product> products;
        private boolean active;

        private Builder() {
        }

        public Builder id(RestaurantId val) {
            id = val;
            return this;
        }

        public Builder products(List<Product> val) {
            products = val;
            return this;
        }

        public Builder active(boolean val) {
            active = val;
            return this;
        }

        public Restaurant build() {
            return new Restaurant(this);
        }
    }
}
