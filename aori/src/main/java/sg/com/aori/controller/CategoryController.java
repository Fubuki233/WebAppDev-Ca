package sg.com.aori.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sg.com.aori.model.Category;
import sg.com.aori.service.CategoryService;

/**
 * REST Controller for Category operations.
 * All tests passed
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.1
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Get all categories.
     * 
     * @return The list of all categories.
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Get category by ID.
     * 
     * @param id The ID of the category to retrieve.
     * @return The category with the specified ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable String id) {
        Optional<Category> category = categoryService.findCategoryById(id);
        return category.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get category by name.
     * 
     * @param name The name of the category to retrieve.
     * @return The category with the specified name.
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable String name) {
        Optional<Category> category = categoryService.findCategoryByName(name);
        return category.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get category by code.
     * 
     * @param code The code of the category to retrieve.
     * @return The category with the specified code.
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<Category> getCategoryByCode(@PathVariable String code) {
        Optional<Category> category = categoryService.findCategoryByCode(code);
        return category.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get category by slug.
     * 
     * @param slug The slug of the category to retrieve.
     * @return The category with the specified slug.
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<Category> getCategoryBySlug(@PathVariable String slug) {
        Optional<Category> category = categoryService.findCategoryBySlug(slug);
        return category.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new category.
     * 
     * @param category The category to create.
     * @return The created category.
     */
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        try {
            if (category.getCategoryId() == null || category.getCategoryId().isEmpty()) {
                category.setCategoryId(java.util.UUID.randomUUID().toString());
            }
            Category createdCategory = categoryService.createCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating category: " + e.getMessage());
        }
    }

    /**
     * Update an existing category.
     * 
     * @param id       The ID of the category to update.
     * @param category The updated category data.
     * @return The updated category.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable String id, @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.updateCategory(id, category);
            return ResponseEntity.ok(updatedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating category: " + e.getMessage());
        }
    }

    /**
     * Delete a category by ID.
     * 
     * @param id The ID of the category to delete.
     * @return The deleted category.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable String id) {
        try {
            Category deletedCategory = categoryService.deleteCategory(id);
            return ResponseEntity.ok(deletedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting category: " + e.getMessage());
        }
    }
}
