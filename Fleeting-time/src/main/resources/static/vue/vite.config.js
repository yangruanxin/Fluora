import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server:{
    hmr:true,
    port:3001,
    proxy:{
      '/api':{
        target:"",
        secure:false,
        changeOrigin:true,
        PathRewrite:{
          '^/api':'/api',
        },
      },
    },
  },
})
