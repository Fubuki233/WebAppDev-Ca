/**
 * Main application component for Aori e-commerce platform.
 * Enhance getPageFromHash to parse the selected category from the URL hash.
 * Add parsing for search (and possibly multiple parameters in the future) and pass them as props to ProductsPage.
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
    const parseProductsRoute = (hashValue) => {
      const queryIndex = hashValue.indexOf('?');
      const params = queryIndex !== -1
        ? new URLSearchParams(hashValue.slice(queryIndex + 1))
        : new URLSearchParams();

      const broadParam = params.get('broad');
      const searchParam = params.get('search');

      return {
        page: 'products',
        broadCategory: broadParam ? broadParam.toLowerCase() : null,
        searchTerm: searchParam ? searchParam.trim() : '',
      };
    };

    if (hash.startsWith('#product/')) {
      const productId = hash.replace('#product/', '');
      return { page: 'product-detail', productId: productId };
    }
    if (hash.startsWith('#products')) {
      return parseProductsRoute(hash);
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
        <ProductsPage
          initialBroadCategory={currentRoute.broadCategory}
          initialSearch={currentRoute.searchTerm}
        />
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
