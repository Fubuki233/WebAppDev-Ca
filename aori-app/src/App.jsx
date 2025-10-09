/**
 * Main application component for Aori e-commerce platform.
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.0
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
    if (hash.startsWith('#product/')) {
      const productId = hash.replace('#product/', '');
      return { page: 'product-detail', productId: productId };
    }
    if (hash === '#products') return { page: 'products' };
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
      {currentRoute.page === 'products' && <ProductsPage />}
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
