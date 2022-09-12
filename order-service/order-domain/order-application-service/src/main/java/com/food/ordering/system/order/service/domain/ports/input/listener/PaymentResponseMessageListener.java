package com.food.ordering.system.order.service.domain.ports.input.listener;

import com.food.ordering.system.order.service.domain.dto.PaymentResponse;

public interface PaymentResponseMessageListener {
    void paymentCompleted(PaymentResponse paymentResponse);
    void paymentCancelled(PaymentResponse paymentResponse);
}
