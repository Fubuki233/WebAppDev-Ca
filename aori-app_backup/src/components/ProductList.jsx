/*  This is for the Product List React Component. To be adjusted further; rough draft by Chat.
    @author Ying Chun
 *  @date 2025-10-07
 *  @version 1.0
 */

import { useEffect, useState } from 'react';
import api from '../services/api';

export default function ProductList() {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    api.get('/products')
      .then(response => setProducts(response.data))
      .catch(err => console.error(err));
  }, []);

  return (
    <div>
      <h1>Products</h1>
      {products.map(p => (
        <div key={p.productId}>
          <h3>{p.productName}</h3>
          <p>{p.description}</p>
        </div>
      ))}
    </div>
  );
}
