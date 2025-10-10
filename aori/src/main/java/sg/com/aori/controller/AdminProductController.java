/**
 * Controller for Product Management (CRUD operations) with Thymeleaf
 * This controller is used by Aori employees to manage the products in the system.
 * 
 * @author Ying Chun
 * @date 2025-10-10 (v2.0)
 * @version 1.0
 * @version 2.0 - Refactored to use Service Layer and add UX improvements
 */

package sg.com.aori.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sg.com.aori.model.Product;
import sg.com.aori.model.Category;
import sg.com.aori.repository.CategoryRepository;
import sg.com.aori.service.CRUDProductService;

@Controller // This enables Spring to resolve view names (e.g. "product-list")
@RequestMapping("/admin/products")
public class AdminProductController {

	// --- DEPENDENCY INJECTION ---
	@Autowired
	private CRUDProductService productService;

	// inject CategoryRepository to populate the category dropdown in the form 
	@Autowired
	private CategoryRepository categoryRepository;

    // --- SHOW ALL PRODUCTS (Read) ---
	@GetMapping ("/")
	public String listAllProducts(Model model) {

		// Get the list from the service. Optional has been used in other controllers, hence we maintain this.
		Optional<List<Product>> optProducts = productService.getAllProducts();

		// This will execute only if optProducts contain a list.
		optProducts.ifPresent(products -> {model.addAttribute("products", products);
		});

		// return the view name 
		return "admin/product-list";
	}

	// --- CREATE NEW PRODUCT (Display Form) ---
	@GetMapping("/new")
	public String showCreateForm(Model model) {

		// Pass a new Product object to the form
		model.addAttribute("product", new Product());

		// Pass the list of all categories to the form for dropdown menu
		List<Category> allCategories = categoryRepository.findAll();
		model.addAttribute("allCategories", allCategories);

		// return the view name
		return "admin/product-form";
	}

	// --- CREATE NEW PRODUCT (Processes Form) ---
	@PostMapping
	public String createProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
		
		// Use ServiceImpl to create the product
		productService.createProduct(product);

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
			List<Category> allCategories = categoryRepository.findAll();
			model.addAttribute("allCategories", allCategories);

			return "admin/product-form"; 
		} else {
			redirectAttributes.addFlashAttribute("error", "Product not found with ID: " + id);
			return "redirect:/admin/products";
		}
	}

	// --- UPDATE EXISTING PRODUCT (Processes form) ---
	@PostMapping("/update/{id}")
	public String updateProduct(@PathVariable String id, @ModelAttribute Product product, RedirectAttributes redirectAttributes) {
		
		productService.updateProduct(id, product);

		redirectAttributes.addFlashAttribute("message", "Product updated successfully!");

		return "redirect:/admin/products";
	}

	// --- DELETE EXISTING PRODUCT ---

	@GetMapping("/delete/{id}")
	public String deleteProduct(@PathVariable String id, @ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        
		productService.deleteProduct(id);

		redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");

        return "redirect:/admin/products";
	}

}
