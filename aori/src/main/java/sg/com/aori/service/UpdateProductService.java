package sg.com.aori.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.aori.interfaces.IProduct.IUpdateProduct;
import sg.com.aori.repository.ProductRepository;
import sg.com.aori.model.Product;

/**
 * Service class for updating products.
 *
 * @author Yunhe
 * @date 2025-10-07
 * @version 1.0
 */
@Service
public class UpdateProductService implements IUpdateProduct {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product updateProduct(String productId, Product product) {
        product.setProductId(productId);
        return productRepository.save(product);
    }

}
