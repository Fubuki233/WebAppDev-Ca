package sg.com.aori.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.aori.model.*;
import sg.com.aori.repository.*;

/**
 * Service for generating product recommendations based on user purchase history
 * 
 * Recommendation Strategy:
 * 1. Find products from same categories as previously purchased/viewed items
 * 2. Exclude products already purchased by the user
 * 3. Prioritize products from frequently purchased categories
 * 4. Include popular products (high ratings) from those categories
 * 
 * @author Yunhe
 * @date 2025-10-12
 * @version 1.0
 */

@Service
public class ProductRecommendationService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private sg.com.aori.repository.ViewHistoryRepository viewHistoryRepository;

    /**
     * Generate product recommendations based on user's purchase history
     * 
     * @param customerId The customer ID
     * @param limit      Maximum number of recommendations to return
     * @return List of recommended products
     */
    public List<Product> getRecommendations(String customerId, int limit) {
        List<Orders> customerOrders = orderRepository.findByCustomerId(customerId);

        if (customerOrders.isEmpty()) {
            return getPopularProducts(limit);
        }

        List<String> orderIds = customerOrders.stream()
                .map(Orders::getOrderId)
                .collect(Collectors.toList());

        List<OrderItem> orderItems = orderItemRepository.findOrderItemsWithProductDetails(orderIds);

        if (orderItems.isEmpty()) {
            return getPopularProducts(limit);
        }

        Set<String> purchasedProductIds = orderItems.stream()
                .map(OrderItem::getProductId)
                .collect(Collectors.toSet());

        Map<String, Integer> categoryFrequency = new HashMap<>();
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            if (product != null && product.getCategoryId() != null) {
                categoryFrequency.merge(product.getCategoryId(), 1, Integer::sum);
            }
        }

        List<Map.Entry<String, Integer>> sortedCategories = categoryFrequency.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        List<Product> recommendations = new ArrayList<>();
        Set<String> addedProductIds = new HashSet<>();

        for (Map.Entry<String, Integer> entry : sortedCategories) {
            String categoryId = entry.getKey();

            List<Product> categoryProducts = productRepository.findByCategoryId(categoryId);

            List<Product> filteredProducts = categoryProducts.stream()
                    .filter(p -> !purchasedProductIds.contains(p.getProductId()))
                    .filter(p -> !addedProductIds.contains(p.getProductId()))
                    .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() > 0) // Only in-stock products
                    .sorted(Comparator.comparing(Product::getRating,
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());

            for (Product product : filteredProducts) {
                if (recommendations.size() >= limit) {
                    break;
                }
                recommendations.add(product);
                addedProductIds.add(product.getProductId());
            }

            if (recommendations.size() >= limit) {
                break;
            }
        }

        if (recommendations.size() < limit) {
            List<Product> popularProducts = getPopularProducts(limit - recommendations.size());
            for (Product product : popularProducts) {
                if (!addedProductIds.contains(product.getProductId()) &&
                        !purchasedProductIds.contains(product.getProductId())) {
                    recommendations.add(product);
                    if (recommendations.size() >= limit) {
                        break;
                    }
                }
            }
        }

        return recommendations;
    }

    /**
     * Get popular products (fallback when no purchase history)
     * 
     * @param limit Maximum number of products to return
     * @return List of popular products sorted by rating
     */
    public List<Product> getPopularProducts(int limit) {
        List<Product> allProducts = productRepository.findAll();

        return allProducts.stream()
                .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() > 0)
                .sorted(Comparator.comparing(Product::getRating,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get recommendations for a specific category based on user history
     * 
     * @param customerId The customer ID
     * @param categoryId The category to get recommendations from
     * @param limit      Maximum number of recommendations
     * @return List of recommended products from the specified category
     */
    public List<Product> getRecommendationsByCategory(String customerId, String categoryId, int limit) {
        List<Orders> customerOrders = orderRepository.findByCustomerId(customerId);

        Set<String> purchasedProductIds = new HashSet<>();
        if (!customerOrders.isEmpty()) {
            List<String> orderIds = customerOrders.stream()
                    .map(Orders::getOrderId)
                    .collect(Collectors.toList());

            List<OrderItem> orderItems = orderItemRepository.findOrderItemsWithProductDetails(orderIds);
            purchasedProductIds = orderItems.stream()
                    .map(OrderItem::getProductId)
                    .collect(Collectors.toSet());
        }

        List<Product> categoryProducts = productRepository.findByCategoryId(categoryId);

        final Set<String> finalPurchasedProductIds = purchasedProductIds;

        return categoryProducts.stream()
                .filter(p -> !finalPurchasedProductIds.contains(p.getProductId()))
                .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() > 0)
                .sorted(Comparator.comparing(Product::getRating,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get similar products based on a specific product
     * (products from the same category, excluding the product itself)
     * 
     * @param productId  The product ID to find similar products for
     * @param customerId The customer ID (to exclude already purchased)
     * @param limit      Maximum number of similar products
     * @return List of similar products
     */
    public List<Product> getSimilarProducts(String productId, String customerId, int limit) {
        Optional<Product> productOpt = productRepository.findById(productId);

        if (productOpt.isEmpty()) {
            return Collections.emptyList();
        }

        Product product = productOpt.get();
        String categoryId = product.getCategoryId();

        if (categoryId == null) {
            return Collections.emptyList();
        }

        Set<String> purchasedProductIds = new HashSet<>();
        if (customerId != null && !customerId.isEmpty()) {
            List<Orders> customerOrders = orderRepository.findByCustomerId(customerId);
            if (!customerOrders.isEmpty()) {
                List<String> orderIds = customerOrders.stream()
                        .map(Orders::getOrderId)
                        .collect(Collectors.toList());

                List<OrderItem> orderItems = orderItemRepository.findOrderItemsWithProductDetails(orderIds);
                purchasedProductIds = orderItems.stream()
                        .map(OrderItem::getProductId)
                        .collect(Collectors.toSet());
            }
        }

        List<Product> categoryProducts = productRepository.findByCategoryId(categoryId);

        final Set<String> finalPurchasedProductIds = purchasedProductIds;

        return categoryProducts.stream()
                .filter(p -> !p.getProductId().equals(productId)) // Exclude the product itself
                .filter(p -> !finalPurchasedProductIds.contains(p.getProductId()))
                .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() > 0)
                .sorted(Comparator.comparing(Product::getRating,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Generate product recommendations based on items currently in the cart
     * 
     * Strategy:
     * 1. Analyze categories of products in cart
     * 2. Find complementary products from same or related categories
     * 3. Exclude products already in cart
     * 4. Prioritize highly rated products
     * 
     * @param customerId The customer ID
     * @param limit      Maximum number of recommendations to return
     * @return List of recommended products based on cart contents
     */
    public List<Product> getRecommendationsFromCart(String customerId, int limit) {
        List<ShoppingCart> cartItems = cartRepository.findByCustomerId(customerId);

        if (cartItems.isEmpty()) {
            return getPopularProducts(limit);
        }

        Set<String> cartProductIds = new HashSet<>();
        Map<String, Integer> categoryFrequency = new HashMap<>();

        for (ShoppingCart cartItem : cartItems) {
            String productId = cartItem.getProductId();
            cartProductIds.add(productId);

            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                if (product.getCategoryId() != null) {
                    categoryFrequency.merge(product.getCategoryId(), 1, Integer::sum);
                }
            }
        }

        List<Map.Entry<String, Integer>> sortedCategories = categoryFrequency.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        Set<String> purchasedProductIds = new HashSet<>();
        List<Orders> customerOrders = orderRepository.findByCustomerId(customerId);
        if (!customerOrders.isEmpty()) {
            List<String> orderIds = customerOrders.stream()
                    .map(Orders::getOrderId)
                    .collect(Collectors.toList());

            List<OrderItem> orderItems = orderItemRepository.findOrderItemsWithProductDetails(orderIds);
            purchasedProductIds = orderItems.stream()
                    .map(OrderItem::getProductId)
                    .collect(Collectors.toSet());
        }

        List<Product> recommendations = new ArrayList<>();
        Set<String> addedProductIds = new HashSet<>();

        for (Map.Entry<String, Integer> entry : sortedCategories) {
            String categoryId = entry.getKey();

            List<Product> categoryProducts = productRepository.findByCategoryId(categoryId);

            final Set<String> finalPurchasedProductIds = purchasedProductIds;

            List<Product> filteredProducts = categoryProducts.stream()
                    .filter(p -> !cartProductIds.contains(p.getProductId()))
                    .filter(p -> !finalPurchasedProductIds.contains(p.getProductId()))
                    .filter(p -> !addedProductIds.contains(p.getProductId()))
                    .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() > 0)
                    .sorted(Comparator.comparing(Product::getRating,
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());

            for (Product product : filteredProducts) {
                if (recommendations.size() >= limit) {
                    break;
                }
                recommendations.add(product);
                addedProductIds.add(product.getProductId());
            }

            if (recommendations.size() >= limit) {
                break;
            }
        }

        if (recommendations.size() < limit) {
            List<Product> popularProducts = getPopularProducts(limit - recommendations.size());
            for (Product product : popularProducts) {
                if (!addedProductIds.contains(product.getProductId()) &&
                        !cartProductIds.contains(product.getProductId())) {
                    recommendations.add(product);
                    if (recommendations.size() >= limit) {
                        break;
                    }
                }
            }
        }

        return recommendations;
    }

    /**
     * Generate product recommendations based on user's browsing/view history
     * 
     * Strategy:
     * 1. Analyze products the user has recently viewed
     * 2. Find categories of viewed products
     * 3. Recommend products from the same categories
     * 4. Prioritize categories that were viewed more frequently or recently
     * 5. Exclude products already viewed by the user
     * 6. Exclude products already purchased
     * 7. Sort by rating (highest first)
     * 8. Only include in-stock products
     * 
     * @param customerId The customer ID
     * @param limit      Maximum number of recommendations to return
     * @return List of recommended products based on browsing history
     */
    public List<Product> getRecommendationsFromViewHistory(String customerId, int limit) {
        List<sg.com.aori.model.ViewHistory> viewHistory = viewHistoryRepository
                .findByUserIdOrderByTimestampDesc(customerId);

        if (viewHistory.isEmpty()) {
            return getPopularProducts(limit);
        }

        Set<String> viewedProductIds = new HashSet<>();
        Map<String, Integer> categoryFrequency = new HashMap<>();
        Map<String, Long> categoryRecency = new HashMap<>();

        for (sg.com.aori.model.ViewHistory view : viewHistory) {
            String productId = view.getProductId();
            viewedProductIds.add(productId);

            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                if (product.getCategoryId() != null) {
                    String categoryId = product.getCategoryId();

                    categoryFrequency.merge(categoryId, 1, Integer::sum);

                    Long viewTime = view.getTimestamp();
                    if (viewTime != null) {
                        categoryRecency.merge(categoryId, viewTime, Math::max);
                    }
                }
            }
        }

        List<Map.Entry<String, Integer>> sortedCategories = categoryFrequency.entrySet()
                .stream()
                .sorted((e1, e2) -> {
                    int freqCompare = e2.getValue().compareTo(e1.getValue());
                    if (freqCompare != 0) {
                        return freqCompare;
                    }
                    Long time1 = categoryRecency.getOrDefault(e1.getKey(), 0L);
                    Long time2 = categoryRecency.getOrDefault(e2.getKey(), 0L);
                    return time2.compareTo(time1);
                })
                .collect(Collectors.toList());

        Set<String> purchasedProductIds = new HashSet<>();
        List<Orders> customerOrders = orderRepository.findByCustomerId(customerId);
        if (!customerOrders.isEmpty()) {
            List<String> orderIds = customerOrders.stream()
                    .map(Orders::getOrderId)
                    .collect(Collectors.toList());

            List<OrderItem> orderItems = orderItemRepository.findOrderItemsWithProductDetails(orderIds);
            purchasedProductIds = orderItems.stream()
                    .map(OrderItem::getProductId)
                    .collect(Collectors.toSet());
        }

        List<Product> recommendations = new ArrayList<>();
        Set<String> addedProductIds = new HashSet<>();

        for (Map.Entry<String, Integer> entry : sortedCategories) {
            String categoryId = entry.getKey();

            List<Product> categoryProducts = productRepository.findByCategoryId(categoryId);

            final Set<String> finalPurchasedProductIds = purchasedProductIds;

            List<Product> filteredProducts = categoryProducts.stream()
                    .filter(p -> !viewedProductIds.contains(p.getProductId()))
                    .filter(p -> !finalPurchasedProductIds.contains(p.getProductId()))
                    .filter(p -> !addedProductIds.contains(p.getProductId()))
                    .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() > 0)
                    .sorted(Comparator.comparing(Product::getRating,
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());

            for (Product product : filteredProducts) {
                if (recommendations.size() >= limit) {
                    break;
                }
                recommendations.add(product);
                addedProductIds.add(product.getProductId());
            }

            if (recommendations.size() >= limit) {
                break;
            }
        }

        if (recommendations.size() < limit) {
            List<Product> popularProducts = getPopularProducts(limit - recommendations.size());
            for (Product product : popularProducts) {
                if (!addedProductIds.contains(product.getProductId()) &&
                        !viewedProductIds.contains(product.getProductId())) {
                    recommendations.add(product);
                    if (recommendations.size() >= limit) {
                        break;
                    }
                }
            }
        }

        return recommendations;
    }
}
