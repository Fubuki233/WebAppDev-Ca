package sg.com.aori.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import sg.com.aori.model.Product;

import java.util.List;

/**
 * Service for searching products with pagination and sorting.
 *
 * @author Simon Lei
 * @date 2025-10-08
 * @version 1.0
 */

/*
 * @author Simon Lei
 * @date 2025-10-14
 * @version 1.1
 */

@Service
public class ProductSearchService {

    @PersistenceContext
    private EntityManager em;

    public List<Product> search(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase();

        String jpql =
            "select p from Product p " +
            "left join fetch p.category c " +
            "where (:q = '' " +
            "   or lower(p.productName) like concat('%', :q, '%') " +
            "   or lower(c.categoryName) like concat('%', :q, '%')) " +
            "order by p.productId desc";

        TypedQuery<Product> tq = em.createQuery(jpql, Product.class)
                .setParameter("q", q);

        return tq.getResultList();
    }
}
