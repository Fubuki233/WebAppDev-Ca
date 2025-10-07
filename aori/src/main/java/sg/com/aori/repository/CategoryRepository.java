package sg.com.aori.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import sg.com.aori.model.Category;

/**
 * Repository interface for Category entity.
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    Category findByCategoryName(String categoryName);

    Category findByCategoryCode(String categoryCode);

    Category findBySlug(String slug);
}
