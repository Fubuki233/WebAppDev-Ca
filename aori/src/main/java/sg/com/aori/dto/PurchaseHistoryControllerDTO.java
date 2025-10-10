package sg.com.aori.dto;

import java.util.List;

import sg.com.aori.model.Orders;
import sg.com.aori.model.Payment;

public class PurchaseHistoryControllerDTO {
    
    private Orders order;
    private List<PurchaseOrderItemDTO> items;
    private List<Payment> payments;
    private boolean partiallyRefunded;
    private Orders.PaymentStatus paymentStatus;

    // 构造函数
    public PurchaseHistoryControllerDTO(Orders order, List<PurchaseOrderItemDTO> items, 
                                         List<Payment> payments, boolean partiallyRefunded, 
                                         Orders.PaymentStatus paymentStatus) {
        this.order = order;
        this.items = items;
        this.payments = payments;
        this.partiallyRefunded = partiallyRefunded;
        this.paymentStatus = paymentStatus;
    }


    public static PurchaseHistoryControllerDTO from(Orders order, List<PurchaseOrderItemDTO> items, 
                                                     List<Payment> payments, boolean partiallyRefunded, 
                                                     Orders.PaymentStatus paymentStatus) {
        return new PurchaseHistoryControllerDTO(order, items, payments, partiallyRefunded, paymentStatus);
    }
}