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
        target: 'http://localhost:8080', // 修改为本地后端服务
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  },
  transpileDependencies: true
});
