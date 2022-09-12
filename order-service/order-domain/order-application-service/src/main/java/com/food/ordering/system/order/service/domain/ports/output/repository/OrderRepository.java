package com.food.ordering.system.order.service.domain.ports.output.repository;

import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;

public interface OrderRepository {
    Order save(Order order);
    Order findByTrackingId(TrackingId trackingId);
}
