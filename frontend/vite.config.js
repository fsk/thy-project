import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      // Development: forward http://localhost:5173/api/... requests to backend (avoids CORS issues)
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
