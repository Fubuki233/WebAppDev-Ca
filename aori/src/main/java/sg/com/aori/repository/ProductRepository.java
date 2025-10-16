package sg.com.aori.repository;

import java.util.List;
import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import sg.com.aori.model.Product;

/**
 * Repository interface for Product entity.
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 * 
 * @author Lei Nuozhen
 * @date 2025-10-08
 * @version 1.1 - Added findByProductNameContainingIgnoreCase
 */

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

    List<Product> findByProductName(String productName);

    List<Product> findByCategoryId(String categoryId);

    List<Product> findByCollection(String collection);

    List<Product> findBySeason(Product.Season season);

    List<Product> findByProductNameContaining(String keyword);

    List<Product> findByCollectionAndSeason(String collection, Product.Season season);

    Page<Product> findByProductNameContainingIgnoreCase(
            String keyword, Pageable pageable);

    Page<Product> findByProductNameContainingIgnoreCaseAndCategoryIdIn(
            String keyword, Collection<String> categoryIds, Pageable pageable);

    @Query("""
                select p from Product p
                left join p.category c
                where (
                    :kw is null
                    or lower(p.productName) like lower(concat('%', :kw, '%'))
                    or (c is not null and lower(c.categoryName) like lower(concat('%', :kw, '%')))
                )
                and (:hasCats = false or p.categoryId in :catIds)
            """)
    Page<Product> searchByKeywordAndCategories(
            @Param("kw") String keyword,
            @Param("catIds") Collection<String> categoryIds,
            @Param("hasCats") boolean hasCategories,
            Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.categoryName = :categoryName")
    List<Product> findProductsByCategoryName(@Param("categoryName") String categoryName);

    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.slug = :slug")
    List<Product> findProductsByCategorySlug(@Param("slug") String slug);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.collection = :collection WHERE p.productId = :productId")
    int updateProductCollection(@Param("productId") String productId, @Param("collection") String collection);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.categoryId = :categoryId")
    long countByCategoryId(@Param("categoryId") String categoryId);

    @Query("SELECT p.id FROM Product p WHERE p.productCode = :productCode")
    String findProductIdByProductCode(@Param("productCode") String productCode);
}
