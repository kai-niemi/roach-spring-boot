package io.roach.spring.order.changefeed;

/**
 * Marker interface for before/after fields
 */
public interface Payload<ID> {
    ID getId();
}
