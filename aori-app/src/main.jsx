/**
 * Main entry point for the Aori React application.
 * 
 * @author Yunhe
 * @date 2025-10-08
 * @version 1.0
 */
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './styles/global.css'
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
    <StrictMode>
        <App />
    </StrictMode>,
)
