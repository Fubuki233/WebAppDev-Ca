package sg.com.aori.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import sg.com.aori.model.Product;
import sg.com.aori.repository.ProductRepository;

/**
 * Admin Portal Controller Class for Aori employees managing product-related in
 * the system.
 * For now, I did not create the service implementation layer yet; need to sync
 * up with Yunhe.
 * To check if the productId should be String or UUID?
 * 
 * @author Ying Chun
 * @date 2025-10-07
 * @version 1.0
 */

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

	@Autowired
	private ProductRepository productRepository;

	@GetMapping
	public String listAllProducts(Model model) { // to check if this is the same as dispProduct()
		model.addAttribute("products", productRepository.findAll());
		return "admin/products-list";
	}

	@GetMapping("/new")
	public String showCreateForm(Model model) {
		model.addAttribute("product", new Product());
		return "admin/product-form";
	}

	@PostMapping
	public String createProduct(@ModelAttribute Product product) {
		productRepository.save(product);
		return "redirect:/admin/products";
	}

	@GetMapping("/edit/{id}")
	public String editProduct(@PathVariable String id, Model model) {
		Product product = productRepository.findById(id).orElseThrow();
		model.addAttribute("product", product);
		return "admin/product-form";
	}

	@PostMapping("/update/{id}")
	public String updateProduct(@PathVariable String id, @ModelAttribute Product product) {
		product.setProductId(id);
		productRepository.save(product);
		return "redirect:/admin/products";
	}

	@GetMapping("/delete/{id}")
	public String deleteProduct(@PathVariable String id) {
		productRepository.deleteById(id);
		return "redirect:/admin/products";
	}

}
