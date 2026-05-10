import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/auth": "http://localhost:8081",
      "/products": "http://localhost:8081",
      "/cart": "http://localhost:8081",
      "/payments": "http://localhost:8081",
      "/orders": "http://localhost:8081"
    }
  }
});
