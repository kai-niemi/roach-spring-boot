package io.roach.spring.trees.category;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.roach.spring.trees.product.Product;

/**
 * JPA implementation for CategoryRepository, partly using the JPA Criteria API.
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class JpaCategoryRepository extends SimpleJpaRepository<Category, Long>
        implements CategoryRepository {

    private final EntityManager em;

    public JpaCategoryRepository(@Autowired EntityManager em) {
        super(Category.class, em);
        this.em = em;
    }

    @Override
    public List<Category> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Category> cq = cb.createQuery(Category.class)
                .distinct(true);
        Root<Category> root = cq.from(Category.class);
        root.fetch("subCategories", JoinType.LEFT);
        TypedQuery<Category> q = em.createQuery(cq);
        return q.getResultList();
    }

    @Override
    public List<Category> findRoots() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Category> cq = cb.createQuery(Category.class)
                .distinct(true);
        Root<Category> root = cq.from(Category.class);
        root.fetch("subCategories", JoinType.LEFT);
        cq.where(cb.isNull(root.get(Category_.parent)));
        TypedQuery<Category> q = em.createQuery(cq);
        return q.getResultList();
    }

    @Override
    public <T extends Category> List<T> findByType(Class<T> categoryType) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(categoryType);
        cq.distinct(true);
        Root<T> root = cq.from(categoryType);
        root.fetch("categorizedProducts", JoinType.LEFT);
        TypedQuery<T> q = em.createQuery(cq);
        return q.getResultList();
    }

    @Override
    public <T extends Category> T getByTypeAndName(Class<T> t, String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(t);
        cq.distinct(true);
        Root<T> root = cq.from(t);
        root.fetch("categorizedProducts", JoinType.LEFT);
        cq.where(cb.equal(root.get(Category_.name), name));
        TypedQuery<T> q = em.createQuery(cq);
        return q.getSingleResult();
    }

    @Override
    public <T extends Category> List<T> findByTypeAndProduct(Class<T> categoryType, Product product) {
        String entityName = categoryType.getSimpleName();

        List<T> categories = em
                .createQuery("select c from " + entityName + " c "
                                + "inner join c.categorizedProducts cp "
                                + "where cp.product =: product "
                                + "and type(c) = :categoryType",
                        categoryType)
                .setParameter("categoryType", categoryType)
                .setParameter("product", product)
                .getResultList();

        return categories;
    }

    @Override
    public <T extends Category> List<T> findAll(Class<T> t) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(t);
        TypedQuery<T> q = em.createQuery(cq);
        return q.getResultList();
    }

    @Override
    public <T extends Category> List<T> findRoots(Class<T> t) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(t);
        Root<T> root = cq.from(t);
        cq.where(cb.isNull(root.get(Category_.parent)));
        TypedQuery<T> q = em.createQuery(cq);
        return q.getResultList();
    }

    @Override
    public long repairTree(Category parent, long left) {
        // The right value of the parent node is left value + 1
        long right = left + 1;
        // Get all children of the parent node
        for (Category child : parent.getChildren()) {
            // Recursive execution of this function for each child node.
            // Right is the current right value, which is incremented by
            // the repairTree method.
            right = repairTree(child, right);
        }
        // We've got the left value, and now that we've processed
        // the children of the parent node we also know the right value.
        parent.setLeft(left);
        parent.setRight(right);
        super.save(parent);
        // Return the right value of the parent node + 1
        return right + 1;
    }

    @Override
    public void createRootNode(Category root) throws DataAccessException {
        Assert.isTrue(root.isRoot(), "Expected root node");
        root = super.save(root);
        // Find highest right value
        Long left = findHighestRight();
        if (left == null) {
            left = 1L; // First root node
        } else {
            left++;
        }
        repairTree(root, left);
    }

    private Long findHighestRight() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Category> qr = cq.from(Category.class);
        cq.select(cb.max(qr.get(Category_.right)));
        TypedQuery<Long> tq = em.createQuery(cq);
        return tq.getSingleResult();
    }

    @Override
    public void insertNode(Category parent, Category child, int index) {
        long right;
        Assert.isTrue(child.isLeaf(), "Child category must be a leaf node");
        if (parent.isLeaf()) {
            right = parent.getLeft();
        } else {
            List<Category> children = parent.getChildren();
            Assert.isTrue(index < children.size(),
                    "Child category index out-of-bounds, max is " + (children.size() - 1));
            Category sibling = children.get(index);
            right = sibling.getRight();
        }
        child.setLeft(right + 1);
        child.setRight(right + 2);
        parent.add(child);

        // Aggregated update of descendant nodes
        Query lq = em.createNativeQuery("update category c set rgt = c.rgt + 2 where c.rgt > :right");
        lq.setParameter("right", right);
        lq.executeUpdate();

        Query rq = em.createNativeQuery("update category c set lft = c.lft + 2 where c.lft > :right");
        rq.setParameter("right", right);
        rq.executeUpdate();
    }

    @Override
    public void deleteNode(Category child) {
        if (child.isNew()) {
            throw new IllegalArgumentException("Transient category not allowed");
        }
        long left = child.getLeft();
        long right = child.getRight();
        long width = child.getRight() - child.getLeft() + 1;

        // Unlink from parent
        if (!child.isRoot()) {
            child.getParent().remove(child);
        }

        // Update edges
        em.createQuery("delete from Category c where c.left between :left and :right")
                .setParameter("left", left)
                .setParameter("right", right)
                .executeUpdate();

        em.createQuery("update Category c set c.right = c.right - :width where c.right > :right")
                .setParameter("right", right)
                .setParameter("width", width)
                .executeUpdate();

        em.createQuery("update Category c set c.left = c.left - :width where c.left > :right")
                .setParameter("right", right)
                .setParameter("width", width)
                .executeUpdate();
    }

    @Override
    public String listTree() {
        Query q = em.createQuery("select n.name, (count(p.name) - 1) as depth from Category n, Category p "
                + "where n.left between p.left and p.right "
                + "group by n.name, n.left "
                + "order by n.left");
        return listPathAsString((List<Object[]>) q.getResultList());
    }

    @Override
    public String listTree(Category parent) {
        Query q = em
                .createNativeQuery("SELECT node.name AS name, (COUNT(parent.name) - (sub_tree.depth + 1)) AS depth"
                        + " FROM category AS node,"
                        + " category AS parent,"
                        + " category AS sub_parent,"
                        + " ("
                        + "   SELECT node.name, (COUNT(parent.name) - 1) AS depth"
                        + "     FROM category AS node,"
                        + "     category AS parent"
                        + "     WHERE node.lft BETWEEN parent.lft AND parent.rgt"
                        + "     AND node.id = :parentId"
                        + "     GROUP BY node.name, node.lft"
                        + "     ORDER BY node.lft"
                        + "     ) AS sub_tree"
                        + "       WHERE node.lft BETWEEN parent.lft AND parent.rgt"
                        + "        AND node.lft BETWEEN sub_parent.lft AND sub_parent.rgt"
                        + "        AND sub_parent.name = sub_tree.name"
                        + "       GROUP BY node.name, node.lft, depth"
                        + "       ORDER BY node.lft");
        q.setParameter("parentId", parent.getId());
        return listPathAsString((List<Object[]>) q.getResultList());
    }

    @Override
    public List<CategoryInfo> listCategoryInfo() {
        Query q = em.createNativeQuery("SELECT "
                + "    parent.id     AS id, "
                + "    parent.name   AS name, "
                + "    COUNT(b.name) AS COUNT "
                + "FROM "
                + "    category                         AS node, "
                + "    category                         AS parent "
                + "LEFT OUTER JOIN categorized_product AS cb "
                + "ON "
                + "    parent.id = cb.category_id "
                + "LEFT OUTER JOIN product AS b "
                + "ON "
                + "    cb.product_id = b.id "
                + "WHERE "
                + "    node.lft BETWEEN parent.lft AND parent.rgt "
                + "GROUP BY "
                + "    parent.name, "
                + "    parent.id, "
                + "    node.lft "
                + "ORDER BY "
                + "    node.lft, parent.name");

        List<CategoryInfo> ci = new ArrayList<>();
        for (Object o : q.getResultList()) {
            Object[] tuple = (Object[]) o;
            Assert.isTrue(tuple.length == 3, "Expected triplicate");
            BigInteger id = (BigInteger) tuple[0];
            String name = (String) tuple[1];
            Number count = (Number) tuple[2];
            ci.add(new CategoryInfo(id.longValue(), name, count.intValue()));
        }
        return ci;
    }

    public String listPathAsString(List<Object[]> path) {
        StringBuffer tree = new StringBuffer();
        for (Object o[] : path) {
            String name = (String) o[0];
            Number depth = (Number) o[1];
            tree.append(new String(new char[depth.intValue()]).replace('\0', ' '));
            tree.append(name);
            tree.append("\n");
        }
        return tree.toString();
    }
}

