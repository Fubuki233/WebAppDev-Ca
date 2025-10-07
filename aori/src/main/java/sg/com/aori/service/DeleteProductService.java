package sg.com.aori.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.aori.interfaces.IProduct.IDeleteProduct;
import sg.com.aori.repository.ProductRepository;
import sg.com.aori.model.Product;

@Service
public class DeleteProductService implements IDeleteProduct {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product deleteProduct(String productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            productRepository.deleteById(productId);
        }
        return product;
    }

}
