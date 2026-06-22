import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// During local dev the React app calls the backend REST API. Requests to /api are
// proxied to the Spring Boot server on :8080 to avoid CORS configuration.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
