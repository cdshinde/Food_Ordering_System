package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.CreatedOrderResponse;
import com.food.ordering.system.order.service.domain.dto.TrackOrderQuery;
import com.food.ordering.system.order.service.domain.dto.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
public class OrderApplicationServiceImpl implements OrderApplicationService {

    public final OrderCreateCommandHandler orderCreateCommandHandler;

    public final OrderTrackCommandHandler orderTrackCommandHandler;

    public OrderApplicationServiceImpl(OrderCreateCommandHandler orderCreateCommandHandler, OrderTrackCommandHandler orderTrackCommandHandler) {
        this.orderCreateCommandHandler = orderCreateCommandHandler;
        this.orderTrackCommandHandler = orderTrackCommandHandler;
    }

    @Override
    public CreatedOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        return orderCreateCommandHandler.createOrder(createOrderCommand);
    }

    @Override
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        return orderTrackCommandHandler.trackOrder(trackOrderQuery);
    }
}
