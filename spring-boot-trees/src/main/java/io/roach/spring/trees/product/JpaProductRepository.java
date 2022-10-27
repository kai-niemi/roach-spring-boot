package io.roach.spring.trees.product;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.roach.spring.trees.category.Category;
import io.roach.spring.trees.category.Category_;

/**
 * JPA implementation for ProductRepository, partly using the JPA Criteria API.
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class JpaProductRepository extends SimpleJpaRepository<Product, Long>
        implements ProductRepository {

    private final EntityManager em;

    public JpaProductRepository(@Autowired EntityManager em) {
        super(Product.class, em);
        this.em = em;
    }

    @Override
    public List<Product> findByKeywordsJPQL(String keywords) {
        // For contrast to criteria API below
        return em.createQuery(
                        "from Product p where p.id "
                                + "in (select p.id from Product p join p.tags t where t in (:tags))",
                        Product.class)
                .setParameter("tags", keywords)
                .getResultList();
    }

    @Override
    public List<Product> findByKeywords(String keywords) {
        // For contrast to JPQL above
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);

        Root<Product> from = criteriaQuery.from(Product.class);
        CriteriaQuery<Product> select = criteriaQuery.select(from);
        Subquery<Long> subQuery = criteriaQuery.subquery(Long.class);

        Root<Product> fromProduct = subQuery.from(Product.class);
        Predicate predicate = criteriaBuilder.in(fromProduct.join("tags")).value(keywords);
        Subquery<Long> subSelect = subQuery.select(fromProduct.get(Product_.id))
                .where(predicate);
        select.where(criteriaBuilder.in(from.get(Product_.id)).value(subSelect));

        TypedQuery<Product> q = em.createQuery(criteriaQuery);
        return q.getResultList();
    }

    @Override
    public List<Product> findByName(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        cq.where(cb.equal(root.get(Product_.name), name));
        TypedQuery<Product> q = em.createQuery(cq);
        return q.getResultList();
    }

    @Override
    public List<Product> findByCategoryType(Class<? extends Category> type) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        cq.distinct(true);
        Root<?> root = cq.from(type);
        Join<Product, CategorizedProduct> join = root.join("categorizedProducts", JoinType.INNER);
        cq.select(join.get(CategorizedProduct_.product));
        TypedQuery<Product> q = em.createQuery(cq);
        return q.getResultList();
    }

    @Override
    public List<Product> findByCategoryTypeAndName(Class<? extends Category> type, String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        cq.distinct(true);
        Root<? extends Category> root = cq.from(type);
        Join<Product, CategorizedProduct> join = root.join("categorizedProducts", JoinType.INNER);
        cq.select(join.get(CategorizedProduct_.product));
        cq.where(cb.equal(root.get(Category_.name), name));
        TypedQuery<Product> q = em.createQuery(cq);
        return q.getResultList();
    }

    @Override
    public List<Product> findByCategories(Collection<Category> categories) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        cq.distinct(true);
        Root<Category> root = cq.from(Category.class);
        root.get(Category_.categorizedProducts);
        Join<Product, CategorizedProduct> join = root.join("categorizedProducts", JoinType.INNER);

        // Compound disjunction predicate for node edges
        Predicate betwenEdges = null;
        for (Category category : categories) {
            Predicate p = cb.between(root.get(Category_.left), category.getLeft(), category.getRight());
            if (betwenEdges == null) {
                betwenEdges = p;
            } else {
                betwenEdges = cb.or(p, betwenEdges);
            }
        }
        cq.select(join.get(CategorizedProduct_.product)).where(betwenEdges);

        TypedQuery<Product> q = em.createQuery(cq);
        return q.getResultList();
    }

    @Override
    public List<Product> findByCategory(Category category) {
        // ¯\_(ツ)_/¯
        final String OQL = "select cp.product from Category as c " +
                "inner join c.categorizedProducts as cp " +
                "where c.left between :leftId and :rightId";
        return em.createQuery(OQL, Product.class)
                .setParameter("leftId", category.getLeft())
                .setParameter("rightId", category.getRight())
                .getResultList();
    }
}
