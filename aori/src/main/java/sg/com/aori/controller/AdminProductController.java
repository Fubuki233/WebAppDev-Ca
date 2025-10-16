package sg.com.aori.controller;

import java.util.*;
import java.util.stream.Collectors;
import jakarta.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;

import sg.com.aori.model.Category;
import sg.com.aori.model.Product;
import sg.com.aori.repository.CategoryRepository;
import sg.com.aori.service.SkuService;
import sg.com.aori.service.CRUDProductService;
import sg.com.aori.utils.SkuTool;

/**
 * Controller for Product Management (CRUD operations) with Thymeleaf.
 * This controller is used by Aori employees to manage the products in the
 * system.
 * 
 * @author Ying Chun
 * @version 1.0
 * 
 * @author Ying Chun
 * @date 2025-10-10
 * @version 2.0 - Refactored to use Service Layer and add UX improvements
 * 
 * @author Ying Chun
 * @version 2.1 - Amended @PathVariable to @RequestParam for delete operation to
 *          enhance security
 * 
 * @author Yunhe
 * @date 2025-10-11
 * @version 2.2 - Added collection display selector for frontend
 * 
 * @author Ying Chun
 * @date 2025-10-14
 * @version 2.3 - Added SKU quantity fetching for product view page.
 */

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

	@Autowired
	private SkuService skuService;

	@Autowired
	private CRUDProductService productService;

	@Autowired
	private CategoryRepository categoryRepository;

	/**
	 * Show all products (Read)
	 */
	@GetMapping(value = { "", "/" })
	public String listAllProducts(Model model,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) String season,
			@RequestParam(required = false) String collection) {

		Page<Product> productPage = productService.findPaginated(page, size, keyword, category, season, collection);
		model.addAttribute("products", productPage);

		model.addAttribute("categories", categoryRepository.findAll());
		List<String> collections = productService.getAllProducts().orElse(List.of()).stream()
				.map(Product::getCollection)
				.distinct()
				.sorted()
				.collect(Collectors.toList());
		model.addAttribute("collections", collections);

		model.addAttribute("selectedCategory", category);
		model.addAttribute("selectedSeason", season);
		model.addAttribute("selectedCollection", collection);
		model.addAttribute("keyword", keyword);

		model.addAttribute("activePage", "products");

		return "admin/products/product-list";
	}

	/**
	 * Create new product (Display Form)
	 */
	@GetMapping("/new")
	public String showCreateForm(Model model) {
		model.addAttribute("product", new Product());
		model.addAttribute("categories", categoryRepository.findAll());
		model.addAttribute("allSizes", Arrays.asList("XS", "S", "M", "L", "XL", "XXL"));
		model.addAttribute("allSeasons", Product.Season.values());
		model.addAttribute("activePage", "products");

		return "admin/products/product-form";
	}

	/**
	 * Create new product (Processes Form)
	 */
	@PostMapping("/save")
	public String saveProduct(@Valid @ModelAttribute("product") Product product, BindingResult bindingResult,
			@RequestParam("sizeJson") String sizeJson,
			@RequestParam("colorJson") String colorJson, @RequestParam("skuQuantitiesJson") String skuQuantitiesJson,
			RedirectAttributes redirectAttributes, Model model) { // Add Model

		try {

			if (bindingResult.hasErrors()) {
				// Re-populate model attributes needed for the form
				model.addAttribute("categories", categoryRepository.findAll());
				model.addAttribute("allSizes", Arrays.asList("XS", "S", "M", "L", "XL", "XXL"));
				model.addAttribute("allSeasons", Product.Season.values());
				model.addAttribute("sizeJson", sizeJson);
				model.addAttribute("colorJson", colorJson);
				model.addAttribute("skuQuantitiesJson", skuQuantitiesJson);

				return "admin/products/product-form";
			}

			product.setSize(sizeJson);
			product.setColors(colorJson);

			String existingProductId = productService.findProductIdByProductCode(product.getProductCode());

			if (existingProductId != null &&
					(product.getProductId() == null || product.getProductId().isEmpty()
							|| !existingProductId.equals(product.getProductId()))) {
				throw new org.springframework.dao.DataIntegrityViolationException("Duplicate product code");
			}
			// Save the product and get the managed entity back.
			product = productService.saveProduct(product);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Map<String, Integer>> skuQuantities = objectMapper.readValue(skuQuantitiesJson,
					new TypeReference<Map<String, Map<String, Integer>>>() {
					});

			for (Map.Entry<String, Map<String, Integer>> colorEntry : skuQuantities.entrySet()) {
				String color = colorEntry.getKey();
				for (Map.Entry<String, Integer> sizeEntry : colorEntry.getValue().entrySet()) {
					String size = sizeEntry.getKey();
					Integer quantity = sizeEntry.getValue();
					String sku = SkuTool.createSku(product.getProductId(), color.replace("#", ""), size,
							productService);
					skuService.createSku(sku, quantity);
				}
			}

			String successMessage = (product.getProductId() != null && !product.getProductId().isEmpty())
					? "Product updated successfully!"
					: "Product created successfully!";
			redirectAttributes.addFlashAttribute("message", successMessage);

			return "redirect:/admin/products";

		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			if (e.getMessage().contains("Duplicate") || e.getMessage().contains("product_code")) {
				model.addAttribute("error", "Failed to save product. A product with the code '"
						+ product.getProductCode() + "' already exists.");
			} else {
				model.addAttribute("error", "Failed to save product due to a database error.");
			}

			model.addAttribute("categories", categoryRepository.findAll());
			model.addAttribute("allSizes", Arrays.asList("XS", "S", "M", "L", "XL", "XXL"));
			model.addAttribute("sizeJson", sizeJson);
			model.addAttribute("allSeasons", Product.Season.values());
			model.addAttribute("colorJson", colorJson);
			model.addAttribute("skuQuantitiesJson", skuQuantitiesJson);

			return "admin/products/product-form";
		} catch (JsonProcessingException e) {
			model.addAttribute("error",
					"Failed to save product. There was an error processing the size, color, or stock quantity data.");

			model.addAttribute("categories", categoryRepository.findAll());
			model.addAttribute("allSizes", Arrays.asList("XS", "S", "M", "L", "XL", "XXL"));
			model.addAttribute("allSeasons", Product.Season.values());
			model.addAttribute("sizeJson", sizeJson);
			model.addAttribute("allSeasons", Product.Season.values());
			model.addAttribute("colorJson", colorJson);
			model.addAttribute("skuQuantitiesJson", skuQuantitiesJson);

			return "admin/products/product-form";
		}
	}

	/**
	 * Edit existing product (Display Form)
	 */
	@GetMapping("edit/{id}")
	public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
		Optional<Product> oneOptProduct = productService.getProductById(id);

		if (oneOptProduct.isPresent()) {
			Product product = oneOptProduct.get();
			model.addAttribute("product", product);
			List<Category> categories = categoryRepository.findAll();

			model.addAttribute("sizeJson", product.getSize());
			model.addAttribute("colorJson", product.getColors());

			// Use LinkedHashMap to preserve order
			Map<String, Map<String, Integer>> skuQuantities = new LinkedHashMap<>();
			List<String> colors = product.getColorsAsList();
			List<String> sizes = product.getSizesAsList();

			System.out.println("[AdminProductController] Edit Form - Product ID: " + product.getProductId());
			System.out.println("[AdminProductController] Edit Form - Colors: " + colors);
			System.out.println("[AdminProductController] Edit Form - Sizes: " + sizes);

			for (String color : colors) {
				Map<String, Integer> sizeQuantityMap = new LinkedHashMap<>();
				for (String size : sizes) {
					String sku = SkuTool.createSku(product.getProductId(), color.replace("#", ""), size,
							productService);
					int quantity = skuService.getQuantity(sku);
					System.out.println("[AdminProductController] Edit Form - SKU: " + sku + " = " + quantity);
					sizeQuantityMap.put(size, quantity);
				}
				skuQuantities.put(color, sizeQuantityMap);
			}
			
			System.out.println("[AdminProductController] Edit Form - SKU Quantities Map: " + skuQuantities);
			model.addAttribute("skuQuantities", skuQuantities);

			model.addAttribute("categories", categories);
			model.addAttribute("allSizes", Arrays.asList("XS", "S", "M", "L", "XL", "XXL"));
			model.addAttribute("allSeasons", Product.Season.values());

			model.addAttribute("activePage", "products");

			return "admin/products/product-form";
		} else {
			redirectAttributes.addFlashAttribute("error", "Product not found with ID: " + id);
			return "redirect:/admin/products";
		}
	}

	/**
	 * Delete existing product
	 */
	@PostMapping("/delete")
	public String deleteProduct(@RequestParam("productId") String id, RedirectAttributes redirectAttributes) {

		System.out.println("Attempting to delete product with ID: " + id);

		try {
			productService.deleteProduct(id);
			redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("error", ex.getMessage());
		}

		return "redirect:/admin/products";
	}

	/**
	 * View single product
	 */
	@GetMapping("/{id}")
	public String viewProduct(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
		Optional<Product> productOpt = productService.getProductById(id);
		if (productOpt.isPresent()) {
			Product product = productOpt.get();
			model.addAttribute("product", product);

			Map<String, Map<String, Integer>> skuQuantities = new TreeMap<>();

			List<String> colors = product.getColorsAsList();
			List<String> sizes = product.getSizesAsList();

			for (String color : colors) {
				Map<String, Integer> sizeQuantityMap = new HashMap<>();
				for (String size : sizes) {
					String sku = SkuTool.createSku(product.getProductId(), color.replace("#", ""), size,
							productService);
					int quantity = skuService.getQuantity(sku);
					System.out.println("[AdminProductController] Fetched quantity for SKU: " + sku + " = " + quantity);
					sizeQuantityMap.put(size, quantity);

				}
				skuQuantities.put(color, sizeQuantityMap);
				System.out.println("[AdmninProductController] SKU Quantities: " + skuQuantities);
			}
			model.addAttribute("skuQuantities", skuQuantities);

			model.addAttribute("activePage", "products");

			return "admin/products/product-view";
		} else {
			redirectAttributes.addFlashAttribute("error", "Product not found with ID: " + id);
			return "redirect:/admin/products";
		}
	}
}

