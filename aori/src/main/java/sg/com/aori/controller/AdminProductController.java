/**
 * Controller for Product Management (CRUD operations) with Thymeleaf
 * This controller is used by Aori employees to manage the products in the system.
 * 
 * @author Ying Chun
 * @date 2025-10-10 (v2.0)
 * @version 1.0
 * @version 2.0 - Refactored to use Service Layer and add UX improvements
 * @version 2.1 - Amended @PathVariable to @RequestParam for delete operation to enhance security
 * 
 * @author Yunhe
 * @date 2025-10-11
 * @version 2.2 - Added collection display selector for frontend
 */

package sg.com.aori.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sg.com.aori.model.Product;
import sg.com.aori.model.Category;
import sg.com.aori.repository.CategoryRepository;
import sg.com.aori.service.CRUDProductService;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

	// --- DEPENDENCY INJECTION ---
	@Autowired
	private CRUDProductService productService;

	// inject CategoryRepository to populate the category dropdown in the form
	@Autowired
	private CategoryRepository categoryRepository;

	// --- SHOW ALL PRODUCTS (Read) ---
	@GetMapping
	public String listAllProducts(Model model,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) String season,
			@RequestParam(required = false) String collection) {

		Page<Product> productPage = productService.findPaginated(page, size, keyword, category, season, collection);
		model.addAttribute("products", productPage);

		// For filter dropdowns
		model.addAttribute("categories", categoryRepository.findAll());
		List<String> collections = productService.getAllProducts().orElse(List.of()).stream()
				.map(Product::getCollection)
				.distinct()
				.sorted()
				.collect(Collectors.toList());
		model.addAttribute("collections", collections);

		// To retain filter values in the form
		model.addAttribute("selectedCategory", category);
		model.addAttribute("selectedSeason", season);
		model.addAttribute("selectedCollection", collection);
		model.addAttribute("keyword", keyword);

		// return the view name
		return "admin/products/product-list";
	}

	// --- CREATE NEW PRODUCT (Display Form) ---
	@GetMapping("/new")
	public String showCreateForm(Model model) {

		// Pass a new Product object to the form
		model.addAttribute("product", new Product());

		// Pass the list of all categories to the form for dropdown menu
		model.addAttribute("categories", categoryRepository.findAll());

		// return the view name
		return "admin/products/product-form";
	}

	// --- CREATE NEW PRODUCT (Processes Form) ---
	@PostMapping("/save")
	public String saveProduct(@ModelAttribute("product") Product product,
			@RequestParam("sizeJson") String sizeJson,
			@RequestParam("colorJson") String colorJson,
			RedirectAttributes redirectAttributes) {

		product.setSize(sizeJson);
		product.setColors(colorJson);

		// Use ServiceImpl to create or update the product
		productService.saveProduct(product);

		// Add success message for user
		redirectAttributes.addFlashAttribute("message", "Product created successfully!");

		return "redirect:/admin/products";
	}

	// --- EDIT EXISTING PRODUCT (Display Form) ---
	@GetMapping("edit/{id}")
	public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
		// Retrieve product by its id
		Optional<Product> oneOptProduct = productService.getProductById(id);

		if (oneOptProduct.isPresent()) {
			// If product is found, add it and the categories to the model
			model.addAttribute("product", oneOptProduct.get());
			List<Category> categories = categoryRepository.findAll();
			model.addAttribute("categories", categories);
			return "admin/products/product-form";
		} else {
			redirectAttributes.addFlashAttribute("error", "Product not found with ID: " + id);
			return "redirect:/admin/products";
		}
	}

	// --- UPDATE EXISTING PRODUCT (Processes form) ---
	// This is now handled by the saveProduct method to unify create and update logic.
	// This simplifies the controller and form.
	// The form will submit to POST /admin/products/save for both new and existing products.
	// The presence of product.productId will determine if it's a create or update.

	/*
	 * Older method before adding business logic to not delete products with orders
	 * // --- DELETE EXISTING PRODUCT ---
	 * 
	 * @GetMapping("/delete/{id}")
	 * public String deleteProduct(@PathVariable String id, @ModelAttribute Product
	 * product, RedirectAttributes redirectAttributes) {
	 * 
	 * productService.deleteProduct(id);
	 * 
	 * redirectAttributes.addFlashAttribute("message",
	 * "Product deleted successfully!");
	 * 
	 * return "redirect:/admin/products";
	 * }
	 */

	// --- DELETE EXISTING PRODUCT ---
	@PostMapping("/delete")
	public String deleteProduct(@RequestParam("productId") String id, RedirectAttributes redirectAttributes) {
		try {
			// Try to delete the product
			productService.deleteProduct(id);
			// If it succeeds, show a success message
			redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");
		} catch (RuntimeException ex) {
			// If the service throws an error (product not found or has orders), catch it
			// and show the error message to the user.
			redirectAttributes.addFlashAttribute("error", ex.getMessage());
		}

		return "redirect:/admin/products";
	}

	// --- VIEW SINGLE PRODUCT ---
	@GetMapping("/{id}")
	public String viewProduct(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
		Optional<Product> productOpt = productService.getProductById(id);
		if (productOpt.isPresent()) {
			model.addAttribute("product", productOpt.get());
			return "admin/products/product-view";
		} else {
			redirectAttributes.addFlashAttribute("error", "Product not found with ID: " + id);
			return "redirect:/admin/products";
		}
	}
}
