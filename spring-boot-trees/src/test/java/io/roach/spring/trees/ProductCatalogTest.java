package io.roach.spring.trees;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import io.roach.spring.trees.category.*;
import io.roach.spring.trees.core.Money;
import io.roach.spring.trees.product.CategorizedProduct;
import io.roach.spring.trees.product.Product;
import io.roach.spring.trees.product.ProductRepository;
import io.roach.spring.trees.product.ProductVariation;
import io.roach.spring.trees.product.ProductVariationRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProductCatalogTest extends AbstractIntegrationTest {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariationRepository productVariationRepository;

    protected Category buildTypeCategories() {
        Category c = new TypeCategory("Types");
        c.add(new TypeCategory("Red"));
        c.add(new TypeCategory("White"));
        c.add(new TypeCategory("Rose"));
        c.add(new TypeCategory("Sparkling"));
        c.add(new TypeCategory("Champagne"));
        c.add(new TypeCategory("Dessert"));
        c.add(new TypeCategory("Box"));
        return c;
    }

    protected Category buildLabelCategories() {
        Category c = new LabelCategory("Label");
        c.add(new LabelCategory("Anis"));
        c.add(new LabelCategory("Honey"));
        c.add(new LabelCategory("Vanilla"));
        c.add(new LabelCategory("Butter"));
        c.add(new LabelCategory("Bread"));
        c.add(new LabelCategory("Young"));
        return c;
    }

    protected Category buildPriceRangeCategories() {
        PriceRangeCategory c = new PriceRangeCategory("Price");
        c.add(new PriceRangeCategory("0-60"));
        c.add(new PriceRangeCategory("61-80"));
        c.add(new PriceRangeCategory("81-100"));
        c.add(new PriceRangeCategory("101-250"));
        c.add(new PriceRangeCategory("251-500"));
        return c;
    }

    protected Category buildCountryCategories() {
        Category c = new DistrictCategory("Country");

        Category e = c.add(new DistrictCategory("Europe"));
        e.add(new DistrictCategory("Italy"));
        e.add(new DistrictCategory("France"));
        e.add(new DistrictCategory("Austria"));
        e.add(new DistrictCategory("Spain"));
        e.add(new DistrictCategory("Germany"));

        Category w = c.add(new DistrictCategory("New World"));
        w.add(new DistrictCategory("South Africa"));
        w.add(new DistrictCategory("Argentina"));
        w.add(new DistrictCategory("Australia"));

        return c;
    }

    protected Category buildProducerCategories() {
        Category c = new ProducerCategory("Producer");
        c.add(new ProducerCategory("Kanonkop"));
        c.add(new ProducerCategory("Masi"));
        c.add(new ProducerCategory("Miguel Torres"));
        return c;
    }

    protected Category buildSpecialCategories() {
        Category c = new SpecialCategory("Special");
        c.add(new SpecialCategory("Recent News"));
        c.add(new SpecialCategory("Findings under 150"));
        c.add(new SpecialCategory("Available in store"));
        return c;
    }

    protected Category buildGrapeCategories() {
        Category c = new GrapeCategory("Grape");
        c.add(new GrapeCategory("Chardonnay"));
        c.add(new GrapeCategory("Riesling"));
        c.add(new GrapeCategory("Merlot"));
        return c;
    }

    @BeforeAll
    public void clearData() {
        productVariationRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAllInBatch();
    }

    // -------------------------------------------------------------------
    // Test methods
    // -------------------------------------------------------------------

    @Test
    @Order(1)
    public void whenCreatingCategories_expectCatalog() {
        categoryRepository.save(buildTypeCategories());
        categoryRepository.save(buildLabelCategories());
        categoryRepository.save(buildCountryCategories());
        categoryRepository.save(buildPriceRangeCategories());
        categoryRepository.save(buildProducerCategories());
        categoryRepository.save(buildSpecialCategories());
        // Leave grapes for now
    }

    @Test
    @Order(2)
    @Transactional
    @Commit
    public void whenRebuildingAdjacencyListOfNodes_expectNestedSetModelComplete() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive(), "wrong tx state");

        List<Category> roots = categoryRepository.findRoots();

        Assertions.assertEquals(6, roots.size());

        long right = 1;
        for (Category c : roots) {
            right = categoryRepository.repairTree(c, right);
        }
    }

    @Test
    @Order(3)
    @Transactional
    @Commit
    public void whenAddingCategory_expectTreeInBalance() {
        Category grapes = buildGrapeCategories();

        categoryRepository.createRootNode(grapes);

        List<Category> roots = categoryRepository.findRoots();
        Assertions.assertEquals(7, roots.size());

        categoryRepository.insertNode(grapes, new GrapeCategory("Pinot gris"), 0);

        Category firstPass = categoryRepository.findById(grapes.getId())
                .orElseThrow(() -> new IllegalStateException(""));
        GrapeCategory grape = firstPass.getChild("Pinot gris", GrapeCategory.class);
        Assertions.assertEquals("Pinot gris", grape.getName());

        categoryRepository.deleteNode(grape);
    }

    @Test
    @Order(4)
    @Transactional
    @Commit
    public void whenListingTree_expectSomeStdOutput() {
        Category grapes = categoryRepository.getByTypeAndName(Category.class, "Grape");
        Category secondPass = categoryRepository.findById(grapes.getId())
                .orElseThrow(() -> new IllegalStateException(""));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> secondPass.getChild("Pinot gris", GrapeCategory.class));

        logger.info("Entire tree:");
        logger.info(categoryRepository.listTree());

        List<Category> roots = categoryRepository.findRoots();

        Assertions.assertEquals(roots.size(), 7);

        for (Category category : roots) {
            Assertions.assertTrue(category.isRoot());
            Assertions.assertFalse(category.isLeaf());
            Assertions.assertTrue(category.getDescendants() > 0);
            Assertions.assertNull(category.getParent());

            logger.info("Category tree rooted at: " + category.getName());
            logger.info(categoryRepository.listTree(category));
        }
    }

    @Test
    @Order(5)
    @Transactional
    @Commit
    public void whenCreatingProducts_expectPersistence() {
        // One product with two variations
        Product p1 = new Product();
        p1.setName("Basilisk Shiraz Mourvedre");
        p1.setSkuCode("BSL-MOURVEDRE");
        p1.addTag("not-bad");
        p1.addTag("not-eco");

        productRepository.save(p1);

        ProductVariation p1v1 = p1.createVariation("BSL-MOURVEDRE-2005", Money.of("16.50", Money.EUR));
        p1v1.setAttribute("color", "red");
        p1v1.setAttribute("taste", "not that terrible");

        ProductVariation p1v2 = p1.createVariation("BSL-MOURVEDRE-2006", Money.of("17.50", Money.EUR));
        p1v2.setAttribute("color", "red");
        p1v2.setAttribute("taste", "not terrible");

        productVariationRepository.save(p1v1);
        productVariationRepository.save(p1v2);

        // One product with no variations

        Product p2 = new Product();
        p2.setName("Pinot Gris Clos Windsbuhl Vendange Tardive Domaine Zind-Humbrecht, 2002");
        p2.setSkuCode("ZIND-HUM-2002");
        p2.addTag("name-not-long-enough");

        productRepository.save(p2);
    }

    private Product findFirstProduct() {
        List<Product> list = productRepository.findByName("Basilisk Shiraz Mourvedre");
        Assertions.assertEquals(list.size(), 1);
        Product p = list.iterator().next();
        Assertions.assertEquals(p.getTags().size(), 2);
        return p;
    }

    @Test
    @Order(6)
    @Transactional
    @Commit
    public void givenFirstProduct_whenAddedToCategories_thenExpectMagic() {
        Product product = findFirstProduct();

        // Link beverage with categories (saved via transparent persistence)
        TypeCategory reds = categoryRepository.getByTypeAndName(TypeCategory.class, "Red");
        assertNotNull(reds);
        reds.addCategorizedProduct(product);

        LabelCategory label = categoryRepository.getByTypeAndName(LabelCategory.class, "Vanilla");
        assertNotNull(label);
        label.addCategorizedProduct(product);

        DistrictCategory district = categoryRepository.getByTypeAndName(DistrictCategory.class, "Germany");
        assertNotNull(district);
        district.addCategorizedProduct(product);
    }

    @Test
    @Order(7)
    @Transactional
    @Commit
    public void whenListingCategoryInfo_expectStdOutput() {
        for (CategoryInfo categoryInfo : categoryRepository.listCategoryInfo()) {
            logger.info(categoryInfo.toString());
        }
    }

    @Test
    @Order(8)
    @Transactional
    @Commit
    public void whenFindingBeveragesByCategoryTypeAndName_expectResults() {
        Category reds = categoryRepository.getByTypeAndName(TypeCategory.class, "Red");
        Assertions.assertEquals(reds.getCategorizedProducts().size(), 1);

        Product product = reds.getCategorizedProducts().iterator().next().getProduct();
        Assertions.assertEquals("Basilisk Shiraz Mourvedre", product.getName());

        Category label = categoryRepository.getByTypeAndName(LabelCategory.class, "Vanilla");
        Assertions.assertEquals(label.getCategorizedProducts().size(), 1);

        product = label.getCategorizedProducts().iterator().next().getProduct();
        Assertions.assertEquals("Basilisk Shiraz Mourvedre", product.getName());

        Category district = categoryRepository.getByTypeAndName(DistrictCategory.class, "Germany");
        Assertions.assertEquals(district.getCategorizedProducts().size(), 1);

        product = district.getCategorizedProducts().iterator().next().getProduct();
        Assertions.assertEquals("Basilisk Shiraz Mourvedre", product.getName());
    }

    @Test
    @Order(9)
    @Transactional
    @Commit
    public void whenFindingBeveragesByCategoryType_expectResults() {
        List<Product> products = productRepository.findByCategoryType(DistrictCategory.class);
        Assertions.assertEquals(products.size(), 1);

        products = productRepository.findByCategoryType(LabelCategory.class);
        Assertions.assertEquals(products.size(), 1);

        products = productRepository.findByCategoryType(DistrictCategory.class);
        Assertions.assertEquals(products.size(), 1);
    }

    @Test
    @Order(10)
    @Transactional
    @Commit
    public void whenFindingBeveragesByMultipleCategories_expectResults() {
        List<Category> categories = new ArrayList<>();

        // Link beverage with categories
        categories.add(categoryRepository.getByTypeAndName(TypeCategory.class, "Red"));
        Assertions.assertEquals(categories.size(), 1);

        categories.add(categoryRepository.getByTypeAndName(LabelCategory.class, "Vanilla"));
        Assertions.assertEquals(categories.size(), 2);

        categories.add(categoryRepository.getByTypeAndName(DistrictCategory.class, "Germany"));
        Assertions.assertEquals(categories.size(), 3);

        List<Product> products = productRepository.findByCategories(categories);
        Assertions.assertEquals(products.size(), 1);
    }

    @Test
    @Order(11)
    @Transactional
    @Commit
    public void whenFindingBeveragesByInheritedCategory_expectResults() {
        // Expects beverages under 'Europe'
        Category category = categoryRepository.getByTypeAndName(DistrictCategory.class, "Europe");
        List<Product> products = productRepository.findByCategory(category);
        Assertions.assertEquals(products.size(), 1);
    }

    @Test
    @Order(12)
    @Transactional
    @Commit
    public void whenFindingByTypeAndBeverage_expectResults() {
        Product firstProduct = findFirstProduct();

        List<TypeCategory> c = categoryRepository.findByTypeAndProduct(TypeCategory.class, firstProduct);
        Assertions.assertEquals(c.size(), 1);

        List<LabelCategory> c2 = categoryRepository.findByTypeAndProduct(LabelCategory.class, firstProduct);
        Assertions.assertEquals(c2.size(), 1);

        List<DistrictCategory> c3 = categoryRepository.findByTypeAndProduct(DistrictCategory.class, firstProduct);
        Assertions.assertEquals(c3.size(), 1);
    }

    @Test
    @Order(13)
    @Transactional
    @Commit
    public void whenPrintingAllCategories_expectStdOutput() {
        logger.info("All categories:");
        List<Category> categories = categoryRepository.findAll();
        for (Category c : categories) {
            logger.info(c.getName());
        }
    }

    @Test
    @Order(14)
    @Transactional
    @Commit
    public void whenPrintingRootCategories_expectStdOutput() {
        logger.info("All root categories:");
        List<Category> categories = categoryRepository.findRoots();
        Assertions.assertEquals(categories.size(), 7);
        for (Category c : categories) {
            logger.info(c.getName());
            for (Category cc : c.getChildren()) {
                logger.info("\t" + cc.getName());
            }
        }
    }

    @Test
    @Order(15)
    @Transactional
    @Commit
    public void whenUpdateBeverageCategories_expectNewStructure() {
        // Link beverage with categories
        {
            TypeCategory reds = categoryRepository.getByTypeAndName(TypeCategory.class, "Red");
            Set<CategorizedProduct> cb = reds.getCategorizedProducts();
            Assertions.assertEquals(cb.size(), 1);
            reds.removeCategorizedProduct(cb.iterator().next());
            categoryRepository.save(reds);
        }

        {
            LabelCategory label = categoryRepository.getByTypeAndName(LabelCategory.class, "Vanilla");
            Set<CategorizedProduct> cb = label.getCategorizedProducts();
            Assertions.assertEquals(cb.size(), 1);
            label.removeCategorizedProduct(cb.iterator().next());
            categoryRepository.save(label);
        }

        {
            DistrictCategory district = categoryRepository.getByTypeAndName(DistrictCategory.class, "Germany");
            Set<CategorizedProduct> cb = district.getCategorizedProducts();
            Assertions.assertEquals(cb.size(), 1);
            district.removeCategorizedProduct(cb.iterator().next());
            categoryRepository.save(district);
        }
    }

    @Test
    @Order(16)
    @Transactional
    @Commit
    public void whenFindingEmptyCategories_expectEmpty() {
        Category reds = categoryRepository.getByTypeAndName(TypeCategory.class, "Red");
        Assertions.assertEquals(reds.getCategorizedProducts().size(), 0);

        Category label = categoryRepository.getByTypeAndName(LabelCategory.class, "Vanilla");
        Assertions.assertEquals(label.getCategorizedProducts().size(), 0);

        Category district = categoryRepository.getByTypeAndName(DistrictCategory.class, "Germany");
        Assertions.assertEquals(district.getCategorizedProducts().size(), 0);
    }

    @Test
    @Order(17)
    @Transactional
    @Commit
    public void whenFindByKeyword_expectResult() {
        List<Product> products = productRepository.findByKeywords("not-bad");
        Assertions.assertEquals(1, products.size());

        products = productRepository.findByKeywordsJPQL("not-bad");
        Assertions.assertEquals(1, products.size());
    }

    @Test
    @Order(18)
    @Transactional
    @Commit
    public void whenTraversingTree_expectSparkles() {
        List<Category> roots = categoryRepository.findRoots();
        Assertions.assertEquals(roots.size(), 7);

        for (Category category : roots) {
            logger.info("Breath-first traversal for {}", category);
            category.breadthFirstTraversal(node -> {
                logger.info(node.toPrettyString());
            });
        }

        for (Category category : roots) {
            logger.info("Depth-first traversal for {}", category);
            category.depthFirstTraversal(node -> {
                logger.info(node.toPrettyString());
            });
        }
    }

    @Test
    @Order(19)
    @Transactional
    @Commit
    public void whenLookingUpVariation_expectOne() {
        ProductVariation pv = productVariationRepository.getBySku("BSL-MOURVEDRE-2005");
        Assertions.assertNotNull(pv);
        Assertions.assertEquals(Money.of("16.50", Money.EUR), pv.getListPrice());
        Assertions.assertEquals("red", pv.getAttributes().get("color"));

        List<ProductVariation> variations = productVariationRepository.findByProductId(findFirstProduct().getId());
        Assertions.assertEquals(2, variations.size());
    }

    @Test
    @Order(20)
    @Transactional
    @Commit
    public void whenListingEntireTree_expectTreeLayout() {
        JdbcTemplate t = new JdbcTemplate();
        t.update("", 2);
        logger.info(categoryRepository.listTree());
    }
}
