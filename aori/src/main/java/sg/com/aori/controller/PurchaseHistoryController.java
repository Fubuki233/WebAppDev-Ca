package sg.com.aori.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sg.com.aori.dto.PurchaseHistoryControllerDTO;
import sg.com.aori.model.OrderItem;
import sg.com.aori.model.Payment;
import sg.com.aori.model.Returns;
import sg.com.aori.service.PurchaseHistoryService;
/**
 * purchasehistory
 *
 * @author Jiayi
 * @date 2025-10-08
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/purchase-history")
public class PurchaseHistoryController {

    @Autowired
    private PurchaseHistoryService purchaseHistoryService;

    /**
     * 根据客户ID和时间区间查询订单历史
     * @param customerId 客户ID
     * @param startDate 查询的开始日期
     * @param endDate 查询的结束日期
     * @param page 页码
     * @param size 每页记录数
     * @return 客户的订单历史数据
     */
    @GetMapping
    public ResponseEntity<Page<PurchaseHistoryControllerDTO>> getPurchaseHistory(
            @RequestParam String customerId,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<PurchaseHistoryControllerDTO> purchaseHistory = (Page<PurchaseHistoryControllerDTO>) purchaseHistoryService.getPurchaseHistory(customerId, startDate, endDate, pageRequest);
        
        if (purchaseHistory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(purchaseHistory);
        }
        return ResponseEntity.ok(purchaseHistory);
    }

    /**
     * 获取订单详情，包括商品信息、支付信息、退货信息等
     * @param orderId 订单ID
     * @return 订单详细信息
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<sg.com.aori.dto.PurchaseHistoryControllerDTO> getOrderDetails(@PathVariable String orderId) {
        sg.com.aori.dto.PurchaseHistoryControllerDTO orderDetails = purchaseHistoryService.getOrderDetails(orderId);

    if (orderDetails == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    return ResponseEntity.ok(orderDetails);
}
    /**
     * 获取订单的支付信息
     * @param orderId 订单ID
     * @return 支付信息
     */
    @GetMapping("/{orderId}/payment")
    public ResponseEntity<List<Payment>> getPaymentDetails(@PathVariable String orderId) {
        List<Payment> payments = purchaseHistoryService.getPaymentDetails(orderId);
        
        if (payments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(payments);
    }

    /**
     * 获取订单的退货信息
     * @param orderId 订单ID
     * @return 退货记录
     */
    @GetMapping("/{orderId}/returns")
    public ResponseEntity<List<Returns>> getReturns(@PathVariable String orderId) {
        List<Returns> returns = purchaseHistoryService.getReturns(orderId);
        
        if (returns.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(returns);
    }

    /**
     * 获取订单项详情，包括商品信息
     * @param orderId 订单ID
     * @return 订单项列表
     */
    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable String orderId) {
        List<OrderItem> orderItems = purchaseHistoryService.getOrderItems(orderId);
        
        if (orderItems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(orderItems);
    }

}
