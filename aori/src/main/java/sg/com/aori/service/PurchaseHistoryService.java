package sg.com.aori.service;
/**
 * purchasehistory
 *
 * @author Jiayi
 * @date 2025-10-08
 * @version 1.0
 */
/**
 * 购买历史 Service 接口
 * 职责：对外提供基于 Customer 的购买历史查询能力（包含取消、未支付、部分退款，时间区间筛选，展开订单行与商品详情）
 */
@SuppressWarnings("unused")
public interface PurchaseHistoryService {

    /**
     * 分页查询指定客户的购买历史
     * - 支持按订单状态/支付状态筛选
     * - 支持按创建时间区间筛选
     * - 结果展开到订单行（含商品详情）
     *
     * @param filter  查询条件（customerId 必填）
     * @param page    页码（从 0 开始）
     * @param size    每页条数（建议 10~50）
     * @return        分页结果（不保证提供 total，总是提供 hasNext 以支持前端“加载更多/下一页”）
     */
    PageResult<PurchaseOrderDTO> getPurchaseHistory(PurchaseHistoryFilter filter, int page, int size);

    /**
     * 查询指定订单（需归属于该客户）的详细信息
     * - 展开订单行（含商品详情/退货标记/部分退款标记）
     *
     * @param customerId 客户 ID
     * @param orderId    订单 ID
     * @return           订单详情 DTO（若订单不存在或不属于该客户，抛出业务异常）
     */
    PurchaseOrderDTO getOrderDetail(String customerId, String orderId);
}
