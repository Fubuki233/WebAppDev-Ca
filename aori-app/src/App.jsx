/**
 * 
 * Main application component for Aori e-commerce platform.
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.0
 * 
 * Enhance getPageFromHash to parse the selected category from the URL hash.
 * @author Sun Rui
 * @date 2025-10-11
 * @version 1.1
 * 
 * Parse the paging parameters in the hash and pass the initial page number/page quantity to ProductsPage.
 * @author Sun Rui
 * @date 2025-10-14
 * @version 1.2
 * 
 * Parse the paging parameters in the hash and pass the initial page number/page quantity to ProductsPage.
 * @author Sun Rui
 * @date 2025-10-16
 * @version 1.3
 */
import { useState, useEffect } from 'react';
import HomePage from "./components/HomePage";
import ProductsPage from "./components/ProductsPage";
import ProductDetailPage from "./components/ProductDetailPage";
import FavouritesPage from "./components/FavouritesPage";
import CartPage from "./components/CartPage";
import CheckoutPage from "./components/CheckoutPage";
import PaymentPage from "./components/PaymentPage";
import LoginPage from "./components/LoginPage";
import RegisterPage from "./components/RegisterPage";
import ProfilePage from "./components/ProfilePage";
import { AuthProvider, useAuth } from "./context/AuthContext";
import './styles/global.css';

function AppContent() {
  const getPageFromHash = () => {
    const hash = window.location.hash;
    const parseProductsRoute = (hashValue) => {
      const queryIndex = hashValue.indexOf('?');
      const params = queryIndex !== -1
        ? new URLSearchParams(hashValue.slice(queryIndex + 1))
        : new URLSearchParams();

      const broadParam = params.get('broad');
      const searchParam = params.get('search');
      const pageParam = params.get('page');
      const limitParam = params.get('limit');

      const parsedPage = pageParam ? parseInt(pageParam, 10) : 1;
      const parsedLimit = limitParam ? parseInt(limitParam, 10) : 12;
      const safePage = Number.isFinite(parsedPage) && parsedPage > 0 ? parsedPage : 1;
      const safeLimit = Number.isFinite(parsedLimit) && parsedLimit > 0 ? parsedLimit : 12;

      return {
        page: 'products',
        broadCategory: broadParam ? broadParam.toLowerCase() : null,
        searchTerm: searchParam ? searchParam.trim() : '',
        pageNumber: safePage,
        pageSize: safeLimit,
      };
    };

    if (hash.startsWith('#product/')) {
      const productId = hash.replace('#product/', '');
      return { page: 'product-detail', productId: productId };
    }
    if (hash.startsWith('#payment/')) {
      const orderId = hash.replace('#payment/', '');
      return { page: 'payment', orderId: orderId };
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

  const { isAuthenticated } = useAuth();

  useEffect(() => {
    const protectedPages = ['favourites', 'checkout', 'profile'];
    if (!isAuthenticated && protectedPages.includes(currentRoute.page)) {
      window.location.hash = '#login';
    }
  }, [currentRoute.page, isAuthenticated]);

  return (
    <div>
      {currentRoute.page === 'home' && <HomePage />}
      {currentRoute.page === 'products' && (
        <ProductsPage
          initialBroadCategory={currentRoute.broadCategory}
          initialSearch={currentRoute.searchTerm}
          initialPage={currentRoute.pageNumber}
          initialLimit={currentRoute.pageSize}
        />
      )}
      {currentRoute.page === 'product-detail' && <ProductDetailPage productId={currentRoute.productId} />}
      {currentRoute.page === 'payment' && <PaymentPage orderId={currentRoute.orderId} />}
      {currentRoute.page === 'favourites' && <FavouritesPage />}
      {currentRoute.page === 'cart' && <CartPage />}
      {currentRoute.page === 'checkout' && <CheckoutPage />}
      {currentRoute.page === 'login' && <LoginPage />}
      {currentRoute.page === 'register' && <RegisterPage />}
      {currentRoute.page === 'profile' && <ProfilePage />}
    </div>
  );
}

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

export default App;
