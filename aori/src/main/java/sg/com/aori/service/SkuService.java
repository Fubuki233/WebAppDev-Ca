package sg.com.aori.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.com.aori.interfaces.ISku;
import sg.com.aori.model.Sku;
import sg.com.aori.repository.SkuRepository;

@Service
public class SkuService implements ISku {
    @Autowired
    private SkuRepository skuRepository;

    @Override
    public String createSku(String sku, int quantity) {
        Sku newSku = new Sku();
        newSku.setSku(sku);
        newSku.setQuantity(quantity);
        skuRepository.save(newSku);
        return sku;
    }

    @Override
    public int getQuantity(String sku) {
        Sku existingSku = skuRepository.findById(sku).orElse(null);
        if (existingSku == null) {
            createSku(sku, 0);
            return 0;

        }
        return existingSku.getQuantity();
    }

    @Override
    public int checkoutSku(String sku) {
        Sku existingSku = skuRepository.findById(sku).orElse(null);
        if (existingSku == null || existingSku.getQuantity() <= 0) {
            return -1;
        }
        existingSku.setQuantity(existingSku.getQuantity() - 1);
        skuRepository.save(existingSku);
        return existingSku.getQuantity();
    }

}
