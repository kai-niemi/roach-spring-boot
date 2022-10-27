package io.roach.spring.trees.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import io.roach.spring.trees.product.Product;

/**
 * JPA repository for categories.
 */
@NoRepositoryBean
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Find categories by type.
     *
     * @param categoryType the category type
     * @return a list of categories
     * @throws org.springframework.dao.DataAccessException on data access failures
     */
    <T extends Category> List<T> findByType(Class<T> categoryType);

    /**
     * Get a category by type and name.
     *
     * @param categoryType the category type
     * @param name the category name
     * @return the category
     * @throws org.springframework.dao.DataAccessException on data access failures
     */
    <T extends Category> T getByTypeAndName(Class<T> categoryType, String name);

    /**
     * Find categories by type and product.
     *
     * @param categoryType the category type
     * @param product the product
     * @return a list of categories
     * @throws org.springframework.dao.DataAccessException on data access failures
     */
    <T extends Category> List<T> findByTypeAndProduct(Class<T> categoryType, Product product);

    /**
     * Find all categories of a given type.
     *
     * @param categoryType the category type
     * @return list of categories
     * @throws org.springframework.dao.DataAccessException on data access failures
     */
    <T extends Category> List<T> findAll(Class<T> categoryType);

    /**
     * Find all root categories (with no ancestors).
     *
     * @return list of all root categories
     * @throws org.springframework.dao.DataAccessException on data access failures
     */
    List<Category> findRoots();

    /**
     * Find all root categories of a given type.
     *
     * @param categoryType the category type
     * @return list of categories
     * @throws org.springframework.dao.DataAccessException on data access failures
     */
    <T extends Category> List<T> findRoots(Class<T> categoryType);

    /**
     * Repairs a tree by converting an adjacency list of category nodes to a
     * nested set model based on modified pre-order tree traversal.
     *
     * @param parent the parent node to start from
     * @param left first number of the first left edge, usually 1 for the first root node
     * @return the right value for the parent node
     * @throws org.springframework.dao.DataAccessException on data access failures
     */
    long repairTree(Category parent, long left);

    /**
     * Create and add a new root category node. The tree will automatically be re-aligned
     * after node creation.
     *
     * @param root the root node to create
     */
    void createRootNode(Category root);

    /**
     * Adds a child to a parent node and updates the nested set model number of
     * all subordinate nodes of the parent node. This method provides more efficient
     * delta updates to category hierarchies. If a category is instead added by just invoking
     * {@link Category#add(Category)}, then the entire tree must be re-aligned
     * with {@link #repairTree(Category, long)}.
     *
     * @param parent the parent node
     * @param child the child node to add
     * @param index the index of the child node
     * @throws org.springframework.dao.DataAccessException on data access failures
     */
    void insertNode(Category parent, Category child, int index);

    /**
     * Delete a child node by unlinking it from its parent node and update the nested set model number of
     * all subordinate nodes of the parent node. This method provides more efficient
     * delta updates to category hierarchies. If a category is instead deleted by just invoking
     * {@link Category#remove(Category)}, then the entire tree must be re-aligned with {@link #repairTree(Category, long)}.
     *
     * @throws org.springframework.dao.DataAccessException on data access failures
     */
    void deleteNode(Category child);

    //---------------------------------------------------
    // Reporting/diagnostic queries
    //---------------------------------------------------

    /**
     * List the entire category tree.
     *
     * @return the category tree as a debug string
     */
    String listTree();

    /**
     * List the subtree of the given root category.
     *
     * @param parent the root category
     * @return the category subtree as a debug string
     */
    String listTree(Category parent);

    List<CategoryInfo> listCategoryInfo();
}
