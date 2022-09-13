package com.food.ordering.system.order.service.domain.ports.input.listener;

import com.food.ordering.system.order.service.domain.dto.RestaurantResponse;

public interface RestaurantApprovalResponseMessageListener {
    void orderApproved(RestaurantResponse restaurantResponse);
    void orderRejected(RestaurantResponse restaurantResponse);
}
