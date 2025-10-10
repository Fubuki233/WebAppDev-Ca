package sg.com.aori.service;

import java.util.List;
/**
 * purchasehistory
 *
 * @author Jiayi
 * @date 2025-10-08
 * @version 1.0
 */
/**
 * 简化的分页结果封装
 * 由于仓库方法返回 List + Pageable，但不返回 total，提供 hasNext 标志位供前端“加载更多/下一页”判断
 */
public class PageResult<T> {

    private final List<T> data;
    private final int page;
    private final int size;
    private final boolean hasNext;

    private PageResult(List<T> data, int page, int size, boolean hasNext) {
        this.data = data;
        this.page = page;
        this.size = size;
        this.hasNext = hasNext;
    }

    public static <T> PageResult<T> of(List<T> data, int page, int size, boolean hasNext) {
        return new PageResult<>(data, page, size, hasNext);
    }

    public List<T> getData() { return data; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public boolean isHasNext() { return hasNext; }
}
