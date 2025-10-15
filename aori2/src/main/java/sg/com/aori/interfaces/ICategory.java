package sg.com.aori.interfaces;

import java.util.List;
import java.util.Optional;

import sg.com.aori.model.Category;

/**
 * Interface for category-related operations.
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 */

public interface ICategory {
    Optional<Category> findCategoryById(String id);

    Optional<Category> findCategoryByName(String name);

    Optional<Category> findCategoryByCode(String code);

    Optional<Category> findCategoryBySlug(String slug);

    List<Category> getAllCategories();

    Category createCategory(Category category);

    Category updateCategory(String categoryId, Category category);

    Category deleteCategory(String categoryId);
}