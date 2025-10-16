import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { logout as logoutApi } from '../api/authApi';

const AuthContext = createContext({
    user: null,
    isAuthenticated: false,
    isProcessing: false,
    login: () => {},
    logout: async () => {},
});

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(() => {
        try {
            const stored = localStorage.getItem('user');
            return stored ? JSON.parse(stored) : null;
        } catch (error) {
            console.error('[AuthContext] Failed to parse stored user:', error);
            return null;
        }
    });
    const [isProcessing, setIsProcessing] = useState(false);

    const persistUser = useCallback((userData) => {
        if (userData) {
            localStorage.setItem('user', JSON.stringify(userData));
            setUser(userData);
        } else {
            localStorage.removeItem('user');
            setUser(null);
        }
    }, []);

    const login = useCallback((userData) => {
        persistUser(userData);
    }, [persistUser]);

    const logout = useCallback(async () => {
        setIsProcessing(true);
        try {
            await logoutApi();
        } catch (error) {
            console.error('[AuthContext] Logout request failed:', error);
        } finally {
            persistUser(null);
            setIsProcessing(false);
        }
    }, [persistUser]);

    useEffect(() => {
        const handleStorage = (event) => {
            if (event.key === 'user') {
                try {
                    const updatedUser = event.newValue ? JSON.parse(event.newValue) : null;
                    setUser(updatedUser);
                } catch (error) {
                    console.error('[AuthContext] Failed to sync user from storage event:', error);
                }
            }
        };

        window.addEventListener('storage', handleStorage);
        return () => window.removeEventListener('storage', handleStorage);
    }, []);

    const value = useMemo(() => ({
        user,
        isAuthenticated: Boolean(user),
        isProcessing,
        login,
        logout,
    }), [user, isProcessing, login, logout]);

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);

export default AuthContext;
