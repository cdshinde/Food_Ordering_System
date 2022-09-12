package com.food.ordering.system.order.service.domain.ports.input.service;

import com.food.ordering.system.order.service.domain.dto.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.CreatedOrderResponse;
import com.food.ordering.system.order.service.domain.dto.TrackOrderQuery;
import com.food.ordering.system.order.service.domain.dto.TrackOrderResponse;

import javax.validation.Valid;

public interface OrderApplicationService {

    CreatedOrderResponse createOrder(@Valid CreateOrderCommand createOrderCommand);

    TrackOrderResponse trackOrder(@Valid TrackOrderQuery trackOrderQuery);
}
