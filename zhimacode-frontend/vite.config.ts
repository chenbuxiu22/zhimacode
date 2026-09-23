import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), vueDevTools()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    // 绑定 IPv4，避免部分浏览器把 localhost 解析为 127.0.0.1 时连不上
    host: '127.0.0.1',
    port: 5173,
    // 端口被占用时直接报错，避免静默换端口后访问到错误地址
    strictPort: true,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8123',
        changeOrigin: true,
        secure: false,
      },
    },
  },
})
