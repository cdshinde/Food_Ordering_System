package com.food.ordering.system.order.service.domain;


import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.dto.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.CreatedOrderResponse;
import com.food.ordering.system.order.service.domain.dto.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) //Check
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

    @Autowired
    private OrderApplicationService orderApplicationService;
    @Autowired
    private OrderDataMapper orderDataMapper;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    private CreateOrderCommand createOrderCommandGood;
    private CreateOrderCommand createOrderCommandWrongPrice;
    private CreateOrderCommand createOrderCommandWrongProductPrice;

    private final UUID Customer_Id = UUID.fromString("980e5ed2-dc48-41c0-b58b-d9b9a598bf9a");
    private final UUID Restaurant_Id = UUID.fromString("c4600986-7481-4dc2-9937-8bc6971711d6");
    private final UUID Product_Id = UUID.fromString("0b3b70f1-4aa7-4b8b-a8f6-22f98d161a7f");
    private final UUID Order_Id = UUID.fromString("a3a19a45-3d50-4bb4-8196-cbcd90f60d17");
    private final BigDecimal Price = new BigDecimal("200.00");

    @BeforeAll
    public void init(){
        createOrderCommandGood = CreateOrderCommand.builder()
                .customerId(Customer_Id)
                .restaurantId(Restaurant_Id)
                .address(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("100AB")
                        .city("Rome")
                        .build())
                .price(Price)
                .orderItems(List.of(OrderItem.builder()
                                .productId(Product_Id)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build(),
                                OrderItem.builder()
                                 .productId(Product_Id)
                                 .quantity(3)
                                 .price(new BigDecimal("50.00"))
                                 .subTotal(new BigDecimal("150.00"))
                                 .build()))
                .build();

        createOrderCommandWrongPrice = CreateOrderCommand.builder()
                .customerId(Customer_Id)
                .restaurantId(Restaurant_Id)
                .address(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("100AB")
                        .city("Rome")
                        .build())
                .price(Price)
                .orderItems(List.of(OrderItem.builder()
                                .productId(Product_Id)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(Product_Id)
                                .quantity(2)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("100.00"))
                                .build()))
                .build();

        createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
                .customerId(Customer_Id)
                .restaurantId(Restaurant_Id)
                .address(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("100AB")
                        .city("Rome")
                        .build())
                .price(Price)
                .orderItems(List.of(OrderItem.builder()
                                .productId(Product_Id)
                                .quantity(1)
                                .price(new BigDecimal("60.00"))
                                .subTotal(new BigDecimal("60.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(Product_Id)
                                .quantity(2)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("100.00"))
                                .build()))
                .build();

    }

    @BeforeEach
    public void beforeEach(){
        Customer customer = new Customer();
        customer.setId(new CustomerId(Customer_Id));

        Restaurant restaurantResponse = Restaurant.builder()
                .id(new RestaurantId(createOrderCommandGood.getRestaurantId()))
                .active(true)
                .products(List.of(
                        new Product(new ProductId(Product_Id), "product1", new Money(new BigDecimal("50.00")))
                )).build();


        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommandGood);
        order.setId(new OrderId(Order_Id));


        when(customerRepository.findCustomer(Customer_Id)).thenReturn(java.util.Optional.of(customer));
        when(restaurantRepository.findRestaurantInfo(orderDataMapper.createOrderCommandToRestaurant(createOrderCommandGood)))
                .thenReturn(Optional.of(restaurantResponse));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
    }

    @Test
    public void testOrderCreated(){
        CreatedOrderResponse createdOrderResponse = orderApplicationService.createOrder(createOrderCommandGood);
        assertEquals(OrderStatus.PENDING, createdOrderResponse.getOrderStatus());
        assertEquals("Created Order", createdOrderResponse.getMessage());
        assertNotNull(createdOrderResponse.getOrderTrackingId());
    }

    @Test
    public void testCreateOrderwithWrongTotalPrice(){
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandWrongPrice));
        assertEquals("Total Price 200.00 is not equals to Order Items total 150.00", orderDomainException.getMessage());

    }

    @Test
    public void testCreateOrderwithWrongProductPrice(){
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandWrongProductPrice));
        assertEquals("OrderItem price 60.00 is not valid for product product1 whose price is 50.00", orderDomainException.getMessage());

    }

    @Test
    public void testCreateOrderwithPassiveRestaurant(){
        Restaurant restaurantResponseActiveFalse = Restaurant.builder()
                .id(new RestaurantId(createOrderCommandGood.getRestaurantId()))
                .active(false)
                .products(List.of(
                        new Product(new ProductId(Product_Id), "product1", new Money(new BigDecimal("50.00")))
                )).build();
        when(restaurantRepository.findRestaurantInfo(orderDataMapper.createOrderCommandToRestaurant(createOrderCommandGood)))
                .thenReturn(Optional.of(restaurantResponseActiveFalse));
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandGood));
        assertEquals("Restaurant with id c4600986-7481-4dc2-9937-8bc6971711d6 is not active", orderDomainException.getMessage());

    }

}
