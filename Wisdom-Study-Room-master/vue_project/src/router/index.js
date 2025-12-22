import { createRouter, createWebHistory } from 'vue-router'
import axios from 'axios'
import { ElMessageBox, ElMessage } from 'element-plus'

const routes = [
  {
    path: '/',
    name: 'Default',
    meta: { title: '智慧自习室' },
    component: () => import('../views/LoginView.vue')
  },
  {
    path: '/LoginView',
    name: 'LoginView',
    meta: { title: '登录 - 智慧自习室' },
    component: () => import('../views/LoginView.vue')
  },
  {
    path: '/RegisterView',
    name: 'RegisterView',
    meta: { title: '注册 - 智慧自习室' },
    component: () => import('../views/RegisterView.vue')
  },
  {
    path: '/HomeView',
    name: 'HomeView',
    meta: { title: '主界面 - 智慧自习室' },
    component: () => import('../views/HomeView.vue'),
    beforeEnter: (to, from, next) => {
      const getUserAccount = sessionStorage.getItem('userAccount')
      if(getUserAccount === null)
      {
        ElMessageBox.alert('请先登录', '提示', {
          confirmButtonText: '确定'
        }).then(() => { next({ name: 'LoginView' }) })
      }
      else
      {
        const url = `user/list?userAccount=${getUserAccount}`
        axios.get(url).then(response => {
          if(response.data != null)
          {
            const violationData = response.data;
            const vio = violationData[0]
            if (vio.userIllegalState === 1)
            {
              ElMessageBox.alert(`用户违规,直到${vio.userIllegalDate}`, '提示', {
                confirmButtonText: '确定'
              }).then(() => { next({ name: 'LoginView' }) })
            }
            else{
              next()
            }
          }
        }).catch(error => {
          console.error('鉴权错误:', error);
          ElMessage({ message: '鉴权错误,请稍后重试', type: 'error' })
        })
      }
    }
  },
  {
    path: '/Admin',
    name: 'Admin',
    meta: { title: '管理员 - 智慧自习室' },
    component: () => import('../views/admin.vue'),
    beforeEnter: (to, from, next) => {
      const getUserAccount = sessionStorage.getItem('userAccount') ;
      if (!getUserAccount) {
        ElMessageBox.alert('请先登录', '提示', {
          confirmButtonText: '去登录'
        }).then(() => { next({ name: 'LoginView' }); });
        return;
      }
      const url = `user/list?userAccount=${getUserAccount}`;
      axios.get(url)
        .then(response => {
          const userData = response.data[0];
          const userPrivilege = userData.userPrivilege;
          if (userPrivilege === 0) {
            ElMessageBox.alert('您没有权限访问管理员界面', '错误', {
              confirmButtonText: '返回'
            }).then(() => { next({ name: 'HomeView' }); });
            return;
          }
          next();
        })
        .catch(error => {
          console.error('鉴权失败:', error);
          ElMessageBox.alert('鉴权失败，请稍后重试或联系管理员', '错误', {
            confirmButtonText: '返回首页'
          }).then(() => { next({ name: 'HomeView' }); });
        });
    }
  },
  {
    path: '/ChangePassword',
    name: 'ChangePassword',
    meta: { title: '更改密码 - 智慧自习室' },
    component: () => import('../views/ChangePassword.vue')
  }

]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
