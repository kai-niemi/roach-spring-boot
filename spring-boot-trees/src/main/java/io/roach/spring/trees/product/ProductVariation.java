package io.roach.spring.trees.product;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

import io.roach.spring.trees.core.AbstractPersistentEntity;
import io.roach.spring.trees.core.Money;

/**
 * Models a variation of a product, i.e color or size variation.
 */
@Entity
@Table(name = "product_variation")
@NamedQueries({
        @NamedQuery(
                name = ProductVariation.BY_PRODUCT_ID,
                query = "from ProductVariation pv "
                        + "where pv.product.id = :productId"
        ),
        @NamedQuery(
                name = ProductVariation.BY_CODE,
                query = "from ProductVariation pv "
                        + "where pv.skuCode = :code"
        ),
        @NamedQuery(
                name = ProductVariation.BY_CODE_EAGER,
                query = "from ProductVariation pv "
                        + "left join fetch pv.product "
                        + "left join fetch pv.attributes "
                        + "where pv.skuCode = :code"
        )
})
public class ProductVariation extends AbstractPersistentEntity<Long> {
    public static final String BY_CODE = "ProductVariation.BY_CODE";

    public static final String BY_PRODUCT_ID = "ProductVariation.BY_PRODUCT_ID";

    public static final String BY_CODE_EAGER = "ProductVariation.BY_CODE_EAGER";

    public static class Builder {
        private Product product;

        private String skuCode;

        private Money price;

        private Map<String, String> attributes = new HashMap<>();

        public Builder withProduct(Product p) {
            this.product = p;
            return this;
        }

        public Builder withSku(String sku) {
            this.skuCode = sku;
            return this;
        }

        public Builder withPrice(Money p) {
            this.price = p;
            return this;
        }

        public ProductVariation build() {
            ProductVariation pv = new ProductVariation(product, skuCode, price);
            pv.getAttributes().putAll(attributes);
            return pv;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku_code", length = 24, nullable = false, unique = true)
    private String skuCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    private Product product;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "list_price", nullable = false)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "currency", length = 3, nullable = false))
    })
    private Money listPrice;

    /**
     * Custom attributes as a key/value map, or an empty map if there are no
     * custom attributes.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "product_variation_attribute",
            joinColumns = @JoinColumn(name = "product_variation_id"))
    @MapKeyColumn(name = "name", length = 55)
    @Column(name = "value", nullable = false, length = 512)
    private Map<String, String> attributes = new HashMap<>();

    protected ProductVariation() {
    }

    protected ProductVariation(Product product, String skuCode, Money listPrice) {
        this.product = product;
        this.skuCode = skuCode;
        this.listPrice = listPrice;
    }

    public Long getId() {
        return id;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public ProductVariation setSkuCode(String code) {
        this.skuCode = code;
        return this;
    }

    public Product getProduct() {
        return product;
    }

    public Money getListPrice() {
        return listPrice;
    }

    public ProductVariation setListPrice(Money listPrice) {
        this.listPrice = listPrice;
        return this;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public ProductVariation setAttribute(String name, String value) {
        attributes.put(name, value);
        return this;
    }
}
