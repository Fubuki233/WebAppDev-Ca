package sg.com.aori.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import sg.com.aori.model.Category;

/**
 * Repository interface for Category entity.
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 * 
 * @author Lei Nuozhen
 * @date 2025-10-09
 * @version 1.1
 */

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    Category findByCategoryName(String categoryName);

    Category findByCategoryCode(String categoryCode);

    Category findBySlug(String slug);

}
