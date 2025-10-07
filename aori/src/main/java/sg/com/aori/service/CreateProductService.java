package sg.com.aori.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.aori.interfaces.IProduct.ICreateProduct;
import sg.com.aori.repository.ProductRepository;
import sg.com.aori.model.Product;

/**
 * Service class for creating products.
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 */

@Service
public class CreateProductService implements ICreateProduct {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

}
