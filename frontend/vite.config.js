import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

/**
 * GitHub Pages (proje sitesi): https://USER.github.io/REPO/ → build'de VITE_BASE_PATH=/REPO/
 * Deploy: npm run deploy (predeploy VITE_BASE_PATH set eder)
 * Local dev: VITE_BASE_PATH verme → base "/", http://localhost:5173/
 */
function viteBase() {
  const raw = process.env.VITE_BASE_PATH;
  if (!raw || raw === "/" || raw === "") return "/";
  const withLeading = raw.startsWith("/") ? raw : `/${raw}`;
  return withLeading.endsWith("/") ? withLeading : `${withLeading}/`;
}

export default defineConfig({
  base: viteBase(),
  plugins: [react()],
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
