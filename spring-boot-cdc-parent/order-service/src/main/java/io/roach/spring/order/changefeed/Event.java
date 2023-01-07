package io.roach.spring.order.changefeed;

/**
 * Marker interface for before/after fields (change events)
 */
public interface Event<ID> {
    ID getId();
}
