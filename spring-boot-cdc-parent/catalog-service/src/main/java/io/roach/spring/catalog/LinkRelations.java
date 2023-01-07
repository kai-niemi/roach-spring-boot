package io.roach.spring.catalog;

public abstract class LinkRelations {
    private LinkRelations() {
    }

    // Application curie name

    public static final String CURIE_NAMESPACE = "catalog";

    // Curie prefixed link relations

    public static final String PRODUCT_REL = "product";

    public static final String PRODUCTS_REL = "products";

    public static final String SCHEDULING_REL = "scheduling";

    public static final String TOGGLE_REL = "toggle";
}
