package sg.com.aori.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.aori.interfaces.ICategory;
import sg.com.aori.model.Category;
import sg.com.aori.repository.CategoryRepository;

/**
 * Service class for category operations.
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 */
@Service
public class CategoryService implements ICategory {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Optional<Category> findCategoryById(String id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Optional<Category> findCategoryByName(String name) {
        return Optional.ofNullable(categoryRepository.findByCategoryName(name));
    }

    @Override
    public Optional<Category> findCategoryByCode(String code) {
        return Optional.ofNullable(categoryRepository.findByCategoryCode(code));
    }

    @Override
    public Optional<Category> findCategoryBySlug(String slug) {
        return Optional.ofNullable(categoryRepository.findBySlug(slug));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category createCategory(Category category) {
        if (category.getCategoryCode() == null || category.getCategoryCode().isEmpty()) {
            throw new IllegalArgumentException("Category code is required");
        }
        if (category.getCategoryName() == null || category.getCategoryName().isEmpty()) {
            throw new IllegalArgumentException("Category name is required");
        }
        if (category.getBroadCategoryId() == null) {
            throw new IllegalArgumentException("Broad category ID is required");
        }

        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(String categoryId, Category category) {
        Optional<Category> existingCategory = categoryRepository.findById(categoryId);
        if (existingCategory.isEmpty()) {
            throw new IllegalArgumentException("Category not found with ID: " + categoryId);
        }
        category.setCategoryId(categoryId);
        return categoryRepository.save(category);
    }

    @Override
    public Category deleteCategory(String categoryId) {
        Optional<Category> existingCategory = categoryRepository.findById(categoryId);
        if (existingCategory.isEmpty()) {
            throw new IllegalArgumentException("Category not found with ID: " + categoryId);
        }

        Category category = existingCategory.get();
        categoryRepository.deleteById(categoryId);
        return category;
    }
}
