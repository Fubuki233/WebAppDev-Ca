# 基于浏览历史的推荐API文档

## 📋 概述

新增的**基于用户浏览历史的推荐系统**,通过分析用户最近浏览过的商品,推荐相关的产品。

---

## 🎯 推荐算法

### 工作原理:
1. **分析浏览历史** - 获取用户最近浏览的所有商品
2. **提取商品分类** - 统计用户浏览过的商品分类
3. **计算分类权重** - 按浏览频率和时间新近度排序
4. **智能过滤**:
   - 排除已浏览的商品
   - 排除已购买的商品
   - 只显示有库存的商品
5. **质量优先** - 按商品评分排序
6. **降级策略** - 无浏览历史时返回热门商品

### 与其他推荐的区别:

| 推荐类型 | 数据源 | 适用场景 |
|---------|--------|---------|
| 购买历史推荐 | 订单数据 | 首页、个人中心 |
| **浏览历史推荐** | **浏览记录** | **首页、发现页** |
| 购物车推荐 | 购物车 | 购物车页面 |
| 相似商品推荐 | 单个商品 | 商品详情页 |

---

## 🔌 API接口

### 后端API

**端点:** `GET /api/products/recommendations/history`

**参数:**
- `customerId` (可选) - 用户UUID,不提供时使用session
- `limit` (可选) - 返回数量,默认10,最大50

**示例请求:**
```http
GET /api/products/recommendations/history?customerId=xxx-xxx-xxx&limit=20
```

**响应:**
```json
[
  {
    "productId": "uuid-1",
    "productName": "Classic T-Shirt",
    "categoryId": "category-uuid",
    "price": 29.99,
    "rating": 4.5,
    "stockQuantity": 50,
    "colors": ["#000000", "#FFFFFF"],
    "size": ["S", "M", "L"],
    "image": "https://...",
    ...
  },
  ...
]
```

---

## 💻 前端使用

### JavaScript API调用

```javascript
import { getViewHistoryRecommendations } from '../api/recommendationApi';

// 基本使用 - 获取10个推荐
const recommendations = await getViewHistoryRecommendations();

// 指定数量
const recommendations = await getViewHistoryRecommendations(20);

// 指定用户ID
const recommendations = await getViewHistoryRecommendations(10, 'user-uuid');
```

### React组件示例

```jsx
import React, { useState, useEffect } from 'react';
import { getViewHistoryRecommendations } from '../api/recommendationApi';
import ProductCard from './ProductCard';

const ViewHistoryRecommendations = () => {
    const [recommendations, setRecommendations] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchRecommendations = async () => {
            try {
                setLoading(true);
                const data = await getViewHistoryRecommendations(12);
                setRecommendations(data);
            } catch (error) {
                console.error('Failed to load recommendations:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchRecommendations();
    }, []);

    if (loading) {
        return <div className="loading">Loading recommendations...</div>;
    }

    if (recommendations.length === 0) {
        return null;
    }

    return (
        <section className="recommendations-section">
            <h2>基于您的浏览推荐</h2>
            <div className="product-grid">
                {recommendations.map(product => (
                    <ProductCard key={product.id} product={product} />
                ))}
            </div>
        </section>
    );
};

export default ViewHistoryRecommendations;
```

---

## 🧪 测试步骤

### 1. 创建浏览历史
首先需要用户浏览一些商品:

```javascript
// 在ProductDetailPage.jsx中,商品被浏览时会自动记录
import { recordViewHistory } from '../api/viewHistoryApi';

// 查看商品时记录浏览历史
await recordViewHistory(productId);
```

### 2. 调用推荐API

```bash
# 使用curl测试
curl -X GET "http://localhost:8080/api/products/recommendations/history?customerId=your-uuid&limit=10" \
  -H "Content-Type: application/json" \
  --cookie "JSESSIONID=xxx"
```

### 3. 在前端使用

```javascript
// 在HomePage或其他页面使用
import { getViewHistoryRecommendations } from './api/recommendationApi';

const HomePage = () => {
    const [recommendations, setRecommendations] = useState([]);

    useEffect(() => {
        const loadRecommendations = async () => {
            const data = await getViewHistoryRecommendations(12);
            setRecommendations(data);
        };
        loadRecommendations();
    }, []);

    return (
        <div>
            <h2>为您推荐</h2>
            <ProductGrid products={recommendations} />
        </div>
    );
};
```

---

## 📊 推荐效果示例

### 场景1: 用户浏览了多个T恤
- 用户浏览: T恤A, T恤B, T恤C (都属于"T恤"分类)
- 推荐结果: 其他高评分的T恤产品

### 场景2: 用户浏览了不同分类
- 用户浏览: 
  - T恤 x3 (权重最高)
  - 牛仔裤 x2 (权重中)
  - 夹克 x1 (权重低)
- 推荐结果: 
  - 优先推荐T恤分类的其他产品
  - 其次推荐牛仔裤
  - 最后推荐夹克

### 场景3: 新用户无浏览历史
- 推荐结果: 返回热门商品(高评分商品)

---

## 🔧 技术实现细节

### 后端Service方法
```java
public List<Product> getRecommendationsFromViewHistory(String customerId, int limit) {
    // 1. 获取用户浏览历史(按时间倒序)
    List<ViewHistory> viewHistory = viewHistoryRepository.findByUserIdOrderByTimestampDesc(customerId);
    
    // 2. 统计分类频率和新近度
    Map<String, Integer> categoryFrequency = new HashMap<>();
    Map<String, Long> categoryRecency = new HashMap<>();
    
    // 3. 按频率和新近度排序分类
    // 4. 从这些分类中推荐高评分商品
    // 5. 排除已浏览和已购买的商品
    // 6. 补充热门商品(如果不足)
    
    return recommendations;
}
```

### 数据库查询
```sql
-- 获取用户浏览历史(按时间倒序)
SELECT * FROM view_history 
WHERE user_id = ? 
ORDER BY timestamp DESC;

-- 获取分类中的商品(排除已浏览和已购买)
SELECT * FROM products 
WHERE category_id = ? 
  AND product_id NOT IN (viewed_products)
  AND product_id NOT IN (purchased_products)
  AND stock_quantity > 0
ORDER BY rating DESC;
```

---

## 🎨 UI集成建议

### 推荐位置:
1. **首页顶部** - "根据您的浏览推荐"
2. **商品列表页** - 侧边栏推荐
3. **个人中心** - "您可能感兴趣"
4. **搜索结果页** - 底部相关推荐

### 显示样式:
```css
.history-recommendations {
    margin: 2rem 0;
    padding: 2rem;
    background: #f5f5f5;
}

.history-recommendations h2 {
    font-size: 1.5rem;
    margin-bottom: 1rem;
    color: #333;
}

.history-recommendations .product-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 1.5rem;
}
```

---

## 📈 性能优化建议

1. **缓存推荐结果** - 使用Redis缓存5-10分钟
2. **限制浏览历史数量** - 只分析最近50-100条浏览记录
3. **异步加载** - 推荐内容延迟加载,不阻塞主页面
4. **批量查询** - 一次性获取所有需要的商品信息

---

## 🔐 权限和隐私

- ✅ 只有登录用户才有个性化推荐
- ✅ 游客用户自动降级为热门商品推荐
- ✅ 用户可以清除浏览历史
- ✅ 遵守隐私政策,不跨用户分享数据

---

## 📞 API总览

所有推荐API:

```javascript
import {
    getRecommendations,              // 基于购买历史
    getViewHistoryRecommendations,   // 基于浏览历史 ⭐ NEW
    getCartRecommendations,          // 基于购物车
    getSimilarProducts,              // 相似商品
    getRecommendationsByCategory,    // 分类推荐
    getPopularProducts               // 热门商品
} from './api/recommendationApi';
```

使用场景对照:

| API方法 | 使用场景 | 数据依赖 |
|--------|---------|---------|
| `getRecommendations()` | 首页、个人中心 | 订单历史 |
| `getViewHistoryRecommendations()` ⭐ | 首页、发现页 | 浏览历史 |
| `getCartRecommendations()` | 购物车页面 | 购物车内容 |
| `getSimilarProducts()` | 商品详情页 | 当前商品 |
| `getRecommendationsByCategory()` | 分类页面 | 分类信息 |
| `getPopularProducts()` | 游客首页 | 商品评分 |

---

## ✅ 完成清单

- [x] 后端Service实现 (`ProductRecommendationService.getRecommendationsFromViewHistory`)
- [x] 后端Controller端点 (`/api/products/recommendations/history`)
- [x] 前端API封装 (`getViewHistoryRecommendations`)
- [x] 算法逻辑:频率+新近度权重
- [x] 智能过滤:排除已浏览/已购
- [x] 降级策略:无历史时返回热门

需要前端集成:
- [ ] 在HomePage中添加推荐区块
- [ ] 创建专门的推荐组件
- [ ] 添加加载状态和错误处理
- [ ] 优化UI显示效果

---

## 🚀 下一步建议

1. **A/B测试** - 对比浏览历史推荐 vs 购买历史推荐的效果
2. **权重优化** - 调整频率和新近度的权重比例
3. **多维度推荐** - 结合浏览历史、购买历史、购物车等
4. **实时更新** - 用户浏览新商品后立即更新推荐
5. **推荐解释** - 告诉用户"为什么推荐这个商品"

---

**作者:** AI Assistant  
**日期:** 2025-10-13  
**版本:** 1.0
