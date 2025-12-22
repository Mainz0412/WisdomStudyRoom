const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  devServer: {
    port: 8081, // 修改为8081端口,避免需要管理员权限
    client: {
      overlay: {
        errors: false,
        warnings: false
      }
    },
    proxy: {
      '/api': {
        // 使用环境变量 VUE_APP_API_URL（在 docker-compose 中设为 http://backend:8080），
        // 在本地开发时可不设置则回退到 localhost:8080
        target: process.env.VUE_APP_API_URL || 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  },
  transpileDependencies: true
});
