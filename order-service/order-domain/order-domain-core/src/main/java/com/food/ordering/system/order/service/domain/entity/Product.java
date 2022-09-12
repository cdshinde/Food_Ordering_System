package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;

/**
 * Product Entity is part of multiple Aggregate, it is part of the Order Aggregate.
 */
public class Product extends BaseEntity<ProductId> {

    private String name;
    private Money price;

    public Product(ProductId productId,String name, Money price) {
        super.setId(productId);
        this.name = name;
        this.price = price;
    }

    public Product(ProductId productId){
        super.setId(productId);
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public void updateDetails(Product restaurantProduct) {
        this.name = restaurantProduct.getName();
        this.price = restaurantProduct.getPrice();
    }
}
