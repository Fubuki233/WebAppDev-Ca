/**
 * Controller for Product Management (CRUD operations) with Thymeleaf.
 * This controller is used by Aori employees to manage the products in the system.
 * 
 * @author Ying Chun
 * @date 2025-10-10 (v2.0)
 * @date 2025-10-14 (v2.3)
 * @version 1.0
 * @version 2.0 - Refactored to use Service Layer and add UX improvements
 * @version 2.1 - Amended @PathVariable to @RequestParam for delete operation to enhance security
 * @version 2.3 - Added SKU quantity fetching for product view page.
 * 
 * @author Yunhe
 * @date 2025-10-11
 * @version 2.2 - Added collection display selector for frontend
 */

package sg.com.aori.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sg.com.aori.model.Category;
import sg.com.aori.model.Product;
import sg.com.aori.repository.CategoryRepository;
import sg.com.aori.service.SkuService;
import sg.com.aori.service.CRUDProductService;
import sg.com.aori.utils.SkuTool;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

	// --- Dependency Injection: SKU Service ---
	@Autowired
	private SkuService skuService;

	// --- Dependency Injection: CRUD Product Service ---
	@Autowired
	private CRUDProductService productService;

	// --- Dependency Injecgtion: CategoryRepository to populate the category dropdown in the form
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

        // Set active page for navigation highlighting
        model.addAttribute("activePage", "products");

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

		// Pass the list of all possible sizes for checkboxes
		model.addAttribute("allSizes", Arrays.asList("XS", "S", "M", "L", "XL", "XXL"));

		// Set active page for navigation highlighting
        model.addAttribute("activePage", "products");

		// return the view name
		return "admin/products/product-form";
	}

	// --- CREATE NEW PRODUCT (Processes Form) ---
	@PostMapping("/save")
	public String saveProduct(@ModelAttribute("product") Product product,
			@RequestParam("sizeJson") String sizeJson,
			@RequestParam("colorJson") String colorJson, 
			@RequestParam(name = "category.categoryId", required = false) String categoryId, // Keep this for category binding
			RedirectAttributes redirectAttributes, Model model) { // Add Model

		try {
			product.setSize(sizeJson);
			product.setColors(colorJson);
			product.setCategoryId(categoryId);

			// --- START: Proactive Duplicate Product Code Check ---
			// Find if another product with the same code already exists.
			String existingProductId = productService.findProductIdByProductCode(product.getProductCode());

			// Check for duplicates. This is a duplicate if:
			// 1. A product with this code exists (existingProductId is not null)
			// 2. We are creating a NEW product (product.getProductId() is empty) OR
			//    we are editing a product but the found ID is DIFFERENT from the one we are editing.
			if (existingProductId != null && 
				(product.getProductId() == null || product.getProductId().isEmpty() || !existingProductId.equals(product.getProductId()))) {
				
				// If it's a duplicate, throw an exception to be caught by the catch block.
				// This is cleaner than duplicating the error handling logic.
				throw new org.springframework.dao.DataIntegrityViolationException("Duplicate product code");
			}
			// --- END: Proactive Duplicate Product Code Check ---

			// Use ServiceImpl to create or update the product
			productService.saveProduct(product);

			// Add success message for user on redirect
			String successMessage = (product.getProductId() != null && !product.getProductId().isEmpty())
					? "Product updated successfully!"
					: "Product created successfully!";
			redirectAttributes.addFlashAttribute("message", successMessage);

			return "redirect:/admin/products";

		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			// This block catches database constraint errors, like duplicate product codes.
			if (e.getMessage().contains("Duplicate") || e.getMessage().contains("product_code")) {
				model.addAttribute("error", "Failed to save product. A product with the code '" + product.getProductCode() + "' already exists.");
			} else {
				model.addAttribute("error", "Failed to save product due to a database error.");
			}
			
			// --- START: Re-populate form data for re-rendering ---
			// We must re-populate the model attributes that the form needs,
			// otherwise it will fail to render.
			model.addAttribute("categories", categoryRepository.findAll());
			model.addAttribute("allSizes", Arrays.asList("XS", "S", "M", "L", "XL", "XXL"));
			model.addAttribute("sizeJson", sizeJson);
			model.addAttribute("colorJson", colorJson);
			// --- END: Re-populate form data ---

			return "admin/products/product-form"; // Return to the form view, not a redirect
		}
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
			// FIX: Pass the raw JSON strings for sizes and colors to the form
			// to prevent Thymeleaf from mis-parsing the list.
			model.addAttribute("sizeJson", oneOptProduct.get().getSize());
			model.addAttribute("colorJson", oneOptProduct.get().getColors());
			model.addAttribute("categories", categories);
			model.addAttribute("allSizes", Arrays.asList("XS", "S", "M", "L", "XL", "XXL"));

			// Set active page for navigation highlighting
        	model.addAttribute("activePage", "products");

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
			Product product = productOpt.get();
			model.addAttribute("product", product);

			// --- SKU Quantities ---
			// We use a TreeMap to keep the colors sorted for a consistent display.
			Map<String, Map<String, Integer>> skuQuantities = new TreeMap<>();

			List<String> colors = product.getColorsAsList();
			List<String> sizes = product.getSizesAsList();

			for (String color : colors) {
				Map<String, Integer> sizeQuantityMap = new HashMap<>();
				for (String size : sizes) {
					String sku = SkuTool.createSku(product.getProductId(), color.replace("#", ""), size, productService);
					int quantity = skuService.getQuantity(sku);
					sizeQuantityMap.put(size, quantity);
				}
				skuQuantities.put(color, sizeQuantityMap);
			}
			model.addAttribute("skuQuantities", skuQuantities);
			// --- END: SKU Quantities ---

			// Set active page for navigation highlighting
        	model.addAttribute("activePage", "products");

			return "admin/products/product-view";
		} else {
			redirectAttributes.addFlashAttribute("error", "Product not found with ID: " + id);
			return "redirect:/admin/products";
		}
	}
}
