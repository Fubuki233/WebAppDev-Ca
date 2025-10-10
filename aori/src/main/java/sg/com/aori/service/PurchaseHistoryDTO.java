package sg.com.aori.service;

import java.util.List;

import sg.com.aori.model.Orders;
import sg.com.aori.model.Payment;

public class PurchaseHistoryDTO {
    public PurchaseHistoryDTO(Orders order, 
                               List<PurchaseOrderItemDTO> itemDTOs, 
                               List<Payment> payments, 
                               boolean partiallyRefunded, 
                               Orders.PaymentStatus paymentStatus) {
        // 初始化相关字段
    }
}
