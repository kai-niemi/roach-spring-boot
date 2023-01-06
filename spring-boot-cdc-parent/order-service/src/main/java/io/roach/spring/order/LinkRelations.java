package io.roach.spring.order;

public abstract class LinkRelations {
    private LinkRelations() {
    }

    // Application curie name

    public static final String CURIE_NAMESPACE = "order";

    // Curie prefixed link relations

    public static final String PRODUCT_REL = "product";

    public static final String FOREIGN_PRODUCT_REL = "foreign-product";

    public static final String PRODUCTS_REL = "products";
}
