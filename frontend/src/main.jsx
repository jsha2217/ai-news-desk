import React from 'react'
import ReactDOM from 'react-dom/client'
import { Toaster } from 'react-hot-toast'
import App from './App.jsx'
import './index.css'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
    <Toaster
      position="top-right"
      toastOptions={{
        duration: 3000,
        style: {
          background: '#1a1f3a',
          color: '#fff',
          border: '1px solid rgba(0, 212, 255, 0.3)',
        },
        success: {
          iconTheme: {
            primary: '#00d4ff',
            secondary: '#1a1f3a',
          },
        },
        error: {
          iconTheme: {
            primary: '#ff4444',
            secondary: '#1a1f3a',
          },
        },
      }}
    />
  </React.StrictMode>,
)
