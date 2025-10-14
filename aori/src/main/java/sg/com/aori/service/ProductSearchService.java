package sg.com.aori.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sg.com.aori.model.Product;
import sg.com.aori.repository.CategoryRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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

    private final CategoryRepository categoryRepository;

    private static final Set<String> COLOR_SET = Set.of(
            "red","blue","green","black","white","yellow","pink","purple","grey","gray","brown","beige","navy","orange"
    );
    private static final Set<String> SIZE_SET = Set.of("xs","s","m","l","xl","2xl","2x","xxl");

    public ProductSearchService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Product> search(String q,
                                String categoryParam,
                                String colorParam,
                                String sizeParam,
                                BigDecimal priceMin,
                                BigDecimal priceMax) {

        String query = (q == null) ? "" : q.trim();
        String qLower = query.toLowerCase(Locale.ROOT);

        Set<String> categoryNames = categoryRepository.findAll().stream()
                .map(c -> Optional.ofNullable(c.getCategoryName()).orElse(""))
                .filter(StringUtils::hasText)
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        String detectedCategory = null;
        String detectedColor = null;
        String detectedSize = null;
        List<String> likeTokens = new ArrayList<>();

        if (StringUtils.hasText(query)) {
            String[] tokens = query.split("\\s+");
            for (String t : tokens) {
                String token = t.toLowerCase(Locale.ROOT);
                if (detectedCategory == null && categoryNames.contains(token)) {
                    detectedCategory = token;
                } else if (detectedColor == null && COLOR_SET.contains(token)) {
                    detectedColor = token;
                } else if (detectedSize == null && SIZE_SET.contains(token)) {
                    detectedSize = token;
                } else {
                    likeTokens.add(token);
                }
            }
        }

        if (StringUtils.hasText(categoryParam)) detectedCategory = categoryParam.toLowerCase(Locale.ROOT);
        if (StringUtils.hasText(colorParam)) detectedColor = colorParam.toLowerCase(Locale.ROOT);
        if (StringUtils.hasText(sizeParam)) detectedSize = sizeParam.toLowerCase(Locale.ROOT);

        StringBuilder jpql = new StringBuilder(
                "select p from Product p left join fetch p.category c where 1=1 ");

        Map<String, Object> params = new HashMap<>();

        for (int i = 0; i < likeTokens.size(); i++) {
            String name = "t" + i;
            jpql.append(" and ( lower(p.productName) like concat('%', :").append(name).append(", '%') ")
                .append(" or  lower(c.categoryName) like concat('%', :").append(name).append(", '%') ) ");
            params.put(name, likeTokens.get(i));
        }

        if (detectedCategory != null) {
            jpql.append(" and lower(c.categoryName) = :category ");
            params.put("category", detectedCategory);
        }
        if (detectedColor != null) {
            jpql.append(" and lower(p.color) = :color ");
            params.put("color", detectedColor);
        }
        if (detectedSize != null) {
            jpql.append(" and lower(p.size) = :size ");
            params.put("size", detectedSize);
        }
        if (priceMin != null) {
            jpql.append(" and p.price >= :priceMin ");
            params.put("priceMin", priceMin);
        }
        if (priceMax != null) {
            jpql.append(" and p.price <= :priceMax ");
            params.put("priceMax", priceMax);
        }

        jpql.append(" order by p.productId desc "); // 如主键不是 productId，按你的字段改

        TypedQuery<Product> tq = em.createQuery(jpql.toString(), Product.class);
        params.forEach(tq::setParameter);

        return tq.getResultList();
    }
}

