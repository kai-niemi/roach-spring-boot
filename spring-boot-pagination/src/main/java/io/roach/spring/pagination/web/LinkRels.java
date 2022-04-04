package io.roach.spring.pagination.web;

/**
 * Domain specific link relations.
 */
public abstract class LinkRels {
    public static final String PRODUCT_REL = "product";

    public static final String PRODUCTS_REL = "products";

    public static final String CUSTOMER_REL = "customer";

    public static final String CUSTOMERS_REL = "customers";

    public static final String ORDER_REL = "order";

    public static final String ORDERS_REL = "orders";

    public static final String ORDER_ITEM_REL = "order-item";

    public static final String ORDERS_ITEMS_REL = "order-items";

    // IANA standard link relations:
    // http://www.iana.org/assignments/link-relations/link-relations.xhtml

    public static final String CURIE_NAMESPACE = "demo-pagination";

    private LinkRels() {
    }
}
