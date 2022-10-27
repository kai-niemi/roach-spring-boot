package io.roach.spring.trees.product;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import io.roach.spring.trees.category.Category;

@NoRepositoryBean
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Generic finders

    List<Product> findByKeywords(String keywords);

    List<Product> findByKeywordsJPQL(String keywords);

    List<Product> findByName(String name);

    // Category finders

    List<Product> findByCategoryType(Class<? extends Category> type);

    List<Product> findByCategoryTypeAndName(Class<? extends Category> type, String name);

    List<Product> findByCategories(Collection<Category> categories);

    List<Product> findByCategory(Category category);
}
