package io.roach.spring.order.product;

import java.util.UUID;

import io.roach.spring.order.changefeed.Envelope;

@FunctionalInterface
public interface ChangeEventListener {
    void onProductChangeEvent(Envelope<ProductPayload, UUID> envelope);
}
