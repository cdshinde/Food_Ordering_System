package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.Aggregate;
import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;

import java.util.List;
import java.util.UUID;

/**
 * According to the DDD Order is an Aggregate Root for the Order Processing User Case.
 * So accessign this Aggregate will happen only through the Aggregate Root OrderId.
 */
public class Order  extends Aggregate<OrderId> {

    private final CustomerId customerId;
    private final RestaurantId restaurantId;
    private final StreetAddress streetAddress;
    private final Money price;
    private final List<OrderItem> orderItems;

    private TrackingId trackingId;
    private OrderStatus orderStatus;
    private List<String> failureMessage;

    private Order(Builder builder) {
        super.setId(builder.orderId);
        customerId = builder.customerId;
        restaurantId = builder.restaurantId;
        streetAddress = builder.streetAddress;
        price = builder.price;
        orderItems = builder.orderItems;
        trackingId = builder.trackingId;
        orderStatus = builder.orderStatus;
        failureMessage = builder.failureMessage;
    }

    public static Builder builder() {
        return new Builder();
    }


    public CustomerId getCustomerId() {
        return customerId;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public StreetAddress getStreetAddress() {
        return streetAddress;
    }

    public Money getPrice() {
        return price;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailureMessage() {
        return failureMessage;
    }

    //When we need to initialize an order we call this method.
    public void initializeOrder(){
        setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        initializeOrderItems();

    }

    private void initializeOrderItems() {
        long itemId = 1;
        for(OrderItem orderItem: orderItems){
            orderItem.initializeOrderItem(super.getId(), new OrderItemId(itemId++));
        }
    }

    //When we want to validate at beginning.
    public void validateOrder(){
        validateInitialOrder();
        validateTotalPrice();
        validateItemsPrice();
    }

    private void validateItemPrice(OrderItem orderItem) {
        if(!orderItem.isPriceValid()){
            throw new OrderDomainException("OrderItem price "+ orderItem.getPrice()+
                    " is not valid for product "+orderItem.getProduct().getName() +
                    " whose price is " +orderItem.getProduct().getPrice());
        }
    }

    private void validateTotalPrice() {
        if(price == null || (price != null && !price.isGreaterThanZero())){
            throw new OrderDomainException("The total price for the order is not valid, should be greater than zero");
        }
    }

    private void validateItemsPrice() {
        Money orderItemsTotal = orderItems.stream().map(orderItem -> {
            validateItemPrice(orderItem);
            return orderItem.getSubTotal();
        }).reduce(Money.ZERO, Money::add);

        if(!price.equals(orderItemsTotal)){
            throw new OrderDomainException("Total Price " + price.getAmount() +
                    " is not equals to Order Items total " + orderItemsTotal.getAmount());
        }
    }



    private void validateInitialOrder() {
        if(orderStatus != null || getId() != null){
            throw new OrderDomainException("Order not in correct state for Initialization");
        }
    }

    //When we are going to pay we will call this method.
    public void pay(){
        if(orderStatus != OrderStatus.PENDING){
            throw new OrderDomainException("Order is not in correct status for Pay Operation to continue");
        }
        orderStatus = OrderStatus.PAID;
    }

    public void approve(){
        if(orderStatus != OrderStatus.PAID){
            throw new OrderDomainException("Order is not in correct status for Approve Operation to continue");
        }
        orderStatus = OrderStatus.APPROVED;
    }

    public void initCancel(List<String> failureMessages){
        if(orderStatus != OrderStatus.PAID){
            throw new OrderDomainException("Order is not in correct status for initCancel Operation to continue");
        }
        orderStatus = OrderStatus.CANCELLING;
        updateFailureMessages(failureMessages);
    }



    public void cancel(List<String> failureMessages){
        if(orderStatus != OrderStatus.CANCELLING || orderStatus != OrderStatus.PENDING) {
            throw new OrderDomainException("Order is not in correct status for initCancel Operation to continue");
        }
        orderStatus = OrderStatus.CANCELLED;
        updateFailureMessages(failureMessages);
    }

    private void updateFailureMessages(List<String> failureMessages) {
        if(failureMessages != null && this.failureMessage != null ){
            this.failureMessage.addAll(failureMessages.stream().filter(message -> !message.isEmpty()).toList());
        }

        if(failureMessages == null){
            this.failureMessage = failureMessages;
        }
    }




    public static final class Builder {
        private OrderId orderId;
        private CustomerId customerId;
        private RestaurantId restaurantId;
        private StreetAddress streetAddress;
        private Money price;
        private List<OrderItem> orderItems;
        private TrackingId trackingId;
        private OrderStatus orderStatus;
        private List<String> failureMessage;

        private Builder() {
        }

        public Builder id(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        public Builder streetAddress(StreetAddress val) {
            streetAddress = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder orderItems(List<OrderItem> val) {
            orderItems = val;
            return this;
        }

        public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder failureMessage(List<String> val) {
            failureMessage = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
