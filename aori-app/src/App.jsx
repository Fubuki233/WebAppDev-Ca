/**
 * Main application component for Aori e-commerce platform.
 * Enhance getPageFromHash to parse the selected category from the URL hash.
 * @author Yunhe, Sun Rui
 * @date 2025-10-11
 * @version 1.1
 */
import { useState, useEffect } from 'react';
import HomePage from "./components/HomePage";
import ProductsPage from "./components/ProductsPage";
import ProductDetailPage from "./components/ProductDetailPage";
import FavouritesPage from "./components/FavouritesPage";
import CartPage from "./components/CartPage";
import CheckoutPage from "./components/CheckoutPage";
import LoginPage from "./components/LoginPage";
import RegisterPage from "./components/RegisterPage";
import ProfilePage from "./components/ProfilePage";
import './styles/global.css';

function App() {
  const getPageFromHash = () => {
    const hash = window.location.hash;
    const parseBroadCategory = (hashValue) => {
      const queryStart = hashValue.indexOf('?');
      if (queryStart === -1) return null;
      const query = hashValue.slice(queryStart + 1);
      const params = new URLSearchParams(query);
      const broad = params.get('broad');
      return broad ? broad.toLowerCase() : null;
    };

    if (hash.startsWith('#product/')) {
      const productId = hash.replace('#product/', '');
      return { page: 'product-detail', productId: productId };
    }
    if (hash.startsWith('#products')) {
      return {
        page: 'products',
        broadCategory: parseBroadCategory(hash),
      };
    }
    if (hash === '#favourites') return { page: 'favourites' };
    if (hash === '#cart') return { page: 'cart' };
    if (hash === '#checkout') return { page: 'checkout' };
    if (hash === '#login') return { page: 'login' };
    if (hash === '#register') return { page: 'register' };
    if (hash === '#profile') return { page: 'profile' };
    return { page: 'home' };
  };

  const [currentRoute, setCurrentRoute] = useState(getPageFromHash());

  useEffect(() => {
    const handleHashChange = () => {
      setCurrentRoute(getPageFromHash());
    };

    window.addEventListener('hashchange', handleHashChange);

    handleHashChange();

    return () => {
      window.removeEventListener('hashchange', handleHashChange);
    };
  }, []);

  return (
    <div>
      {currentRoute.page === 'home' && <HomePage />}
      {currentRoute.page === 'products' && (
        <ProductsPage initialBroadCategory={currentRoute.broadCategory} />
      )}
      {currentRoute.page === 'product-detail' && <ProductDetailPage productId={currentRoute.productId} />}
      {currentRoute.page === 'favourites' && <FavouritesPage />}
      {currentRoute.page === 'cart' && <CartPage />}
      {currentRoute.page === 'checkout' && <CheckoutPage />}
      {currentRoute.page === 'login' && <LoginPage />}
      {currentRoute.page === 'register' && <RegisterPage />}
      {currentRoute.page === 'profile' && <ProfilePage />}
    </div>
  );
}

export default App;
