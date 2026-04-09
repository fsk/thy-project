import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

/**
 * GitHub Pages proje sitesi: https://user.github.io/REPO/
 * Build sirasinda: VITE_BASE_PATH=/REPO/  (basinda ve sonunda / olmali sekilde normalize edilir)
 * User site (REPO = owner.github.io): VITE_BASE_PATH=/ veya hic verme
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
