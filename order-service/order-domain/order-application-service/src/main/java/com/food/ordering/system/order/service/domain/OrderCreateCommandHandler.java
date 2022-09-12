package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.CreatedOrderResponse;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import com.food.ordering.system.order.service.domain.service.OrderDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderCreateCommandHandler {

    private final OrderDomainService orderDomainService;

    private final OrderRepository orderRepository;

    private final CustomerRepository customerRepository;

    private final RestaurantRepository restaurantRepository;

    private final OrderDataMapper orderDataMapper;

    public OrderCreateCommandHandler(OrderDomainService orderDomainService,
                                        OrderRepository orderRepository,
                                        CustomerRepository customerRepository,
                                        RestaurantRepository restaurantRepository, OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderDataMapper = orderDataMapper;
    }

    @Transactional
    public CreatedOrderResponse createOrder(CreateOrderCommand createOrderCommand){
        checkCustomer(createOrderCommand.getCustomerId());
        Restaurant restaurant = checkRestaurant(createOrderCommand);
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant);
        Order orderResult = saveOrder(order);
        return orderDataMapper.convertOrderToCreateOrderReponse(orderResult, "Created Order");

    }

    private Order saveOrder(Order order) {
        Order orderRes = orderRepository.save(order);
        if(orderRes == null){
            log.warn("Order not saved for order id, {} ", order.getId().getValue());
            throw new OrderDomainException("Order not saved for order id "+ order.getId().getValue());
        }
        log.info("Order saved with order Id, {}", orderRes.getId().getValue());
        return orderRes;
    }

    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
        Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        Optional<Restaurant> restaurantOptional = restaurantRepository.findRestaurantInfo(restaurant);
        if(restaurantOptional.isEmpty()){
            log.warn("No restaurant details present for restaurantId: {}", restaurant.getId());
            throw new OrderDomainException("No restaurant details present for restaurantId "+ restaurant.getId());
        }
        return restaurantOptional.get();
    }

    private void checkCustomer(UUID customerId) {
        Optional<Customer> customer = customerRepository.findCustomer(customerId);
        if(customer.isEmpty()){
            log.warn("The customer is not present for customerId: {}", customerId);
            throw new OrderDomainException("The customer is not present for customerId "+ customerId);
        }
    }

}
