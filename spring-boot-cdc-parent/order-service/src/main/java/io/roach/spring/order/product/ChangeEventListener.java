package io.roach.spring.order.product;

import java.util.UUID;

import io.roach.spring.order.changefeed.Payload;

@FunctionalInterface
public interface ChangeEventListener {
    void onProductChangeEvent(Payload<ProductEvent, UUID> payload);
}
