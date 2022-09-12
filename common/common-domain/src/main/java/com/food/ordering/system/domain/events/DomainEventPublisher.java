package com.food.ordering.system.domain.events;

public interface DomainEventPublisher<T extends DomainEvents> {
    void publish(T domainEvent);
}
