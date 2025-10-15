package sg.com.aori.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sg.com.aori.model.OrderItem;
import sg.com.aori.model.Orders;
import sg.com.aori.repository.OrderRepository;
import sg.com.aori.service.OrderService;

/**
 * Controller for Order Management in Admin Portal.
 * This controller is used by Aori employees to view and manage orders in the
 * system.
 * 
 * @author Yunhe
 * @date 2025-10-15
 * @version 1.0
 */

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    /**
     * Display all orders with pagination and filtering
     */
    @GetMapping
    public String listAllOrders(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) String paymentStatus) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Orders> orderPage;

        if ((keyword != null && !keyword.isEmpty()) ||
                (orderStatus != null && !orderStatus.isEmpty()) ||
                (paymentStatus != null && !paymentStatus.isEmpty())) {

            List<Orders> allOrders = orderRepository.findAll(Sort.by("createdAt").descending());

            if (keyword != null && !keyword.isEmpty()) {
                allOrders = allOrders.stream()
                        .filter(order -> {
                            boolean matchesOrderNumber = order.getOrderNumber() != null &&
                                    order.getOrderNumber().toLowerCase().contains(keyword.toLowerCase());
                            boolean matchesCustomerId = order.getCustomerId() != null &&
                                    order.getCustomerId().toLowerCase().contains(keyword.toLowerCase());
                            return matchesOrderNumber || matchesCustomerId;
                        })
                        .toList();
            }

            if (orderStatus != null && !orderStatus.isEmpty()) {
                Orders.OrderStatus status = Orders.OrderStatus.valueOf(orderStatus);
                allOrders = allOrders.stream()
                        .filter(order -> order.getOrderStatus().equals(status))
                        .toList();
            }

            if (paymentStatus != null && !paymentStatus.isEmpty()) {
                Orders.PaymentStatus pStatus = Orders.PaymentStatus.valueOf(paymentStatus);
                allOrders = allOrders.stream()
                        .filter(order -> order.getPaymentStatus().equals(pStatus))
                        .toList();
            }

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), allOrders.size());
            List<Orders> pageContent = allOrders.subList(start, end);

            orderPage = new org.springframework.data.domain.PageImpl<>(
                    pageContent, pageable, allOrders.size());

        } else {
            orderPage = orderRepository.findAll(pageable);
        }

        model.addAttribute("orders", orderPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedOrderStatus", orderStatus);
        model.addAttribute("selectedPaymentStatus", paymentStatus);

        model.addAttribute("orderStatuses", Orders.OrderStatus.values());
        model.addAttribute("paymentStatuses", Orders.PaymentStatus.values());

        model.addAttribute("activePage", "orders");

        return "admin/orders/order-list";
    }

    /**
     * View single order details
     */
    @GetMapping("/{id}")
    public String viewOrder(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        Orders order = orderRepository.findByIdWithCustomer(id).orElse(null);

        if (order == null) {
            redirectAttributes.addFlashAttribute("error", "Order not found with ID: " + id);
            return "redirect:/admin/orders";
        }

        List<OrderItem> orderItems = orderService.findOrderItemsByOrderId(id);

        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItems);

        model.addAttribute("activePage", "orders");

        return "admin/orders/order-view";
    }

    /**
     * Update order status
     */
    @PostMapping("/{id}/update-status")
    public String updateOrderStatus(
            @PathVariable String id,
            @RequestParam("orderStatus") String orderStatus,
            RedirectAttributes redirectAttributes) {

        try {
            Orders.OrderStatus status = Orders.OrderStatus.valueOf(orderStatus);
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("message", "Order status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update order status: " + e.getMessage());
        }

        return "redirect:/admin/orders/" + id;
    }
}
