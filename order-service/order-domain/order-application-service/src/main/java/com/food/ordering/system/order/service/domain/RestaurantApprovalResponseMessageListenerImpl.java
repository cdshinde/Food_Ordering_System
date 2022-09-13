package com.food.ordering.system.order.service.domain;


import com.food.ordering.system.order.service.domain.dto.RestaurantResponse;
import com.food.ordering.system.order.service.domain.ports.input.listener.RestaurantApprovalResponseMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
public class RestaurantApprovalResponseMessageListenerImpl implements RestaurantApprovalResponseMessageListener {
    @Override
    public void orderApproved(RestaurantResponse restaurantResponse) {

    }

    @Override
    public void orderRejected(RestaurantResponse restaurantResponse) {

    }
}
