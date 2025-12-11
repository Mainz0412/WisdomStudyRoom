    <template>
        <div id="building">
            <img src="../assets/logo.png" class="logo" alt="logo"> <!-- 添加徽标 -->
            <div class="register-container">
                <el-button type="text" class="return" @click="goToLogin">返回</el-button>
                <div class="head">
                    <h2>用户注册</h2>
                </div>
                <el-form :model="form" :rules="rules" ref="registerForm" class="register-form" label-width="80px">
                    <el-form-item label="用户名" prop="username">
                        <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
                    </el-form-item>
                    <el-form-item label="账号" prop="useraccount">
                        <el-input v-model="form.useraccount" placeholder="请输入账号"></el-input>
                    </el-form-item>
                    <el-form-item label="密码" prop="userpassword">
                        <el-input type="password" v-model="form.userpassword" placeholder="请输入密码" clearable
                            show-password></el-input>
                    </el-form-item>
                    <el-form-item label="确认密码" prop="confirmPassword">
                        <el-input type="password" v-model="form.confirmPassword" placeholder="请确认密码" clearable
                            show-password></el-input>
                    </el-form-item>
                    <el-button type="primary" style="width: 90%" @click="register">注 册</el-button>

                </el-form>
            </div>
        </div>
    </template>
<script>

import axios from "axios";
export default {
  data () {
    return {
      form: {
        username: '',
        useraccount: '',
        userpassword: '',
        confirmPassword:''
      },
      rules: {
        username: [
          {
            required: true,
            message: '请输入用户名',
            trigger: 'blur'
          }
        ],
        useraccount: [
          {
            required: true,
            message: '请输入账号',
            trigger: 'blur'
          }
        ],
        userpassword: [
          {
            required: true,
            message: '请输入密码',
            trigger: 'blur'
          }
        ],
        confirmPassword: [
          {
            required: true,
            message: '请确认密码',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  mounted() {
    document.title = this.$route.meta.title;
  },
  methods: {
    register () {
      this.$refs.registerForm.validate((valid) => {
        if (valid) {
          if (this.form.userpassword !== this.form.confirmPassword) {
            this.$message.error('两次密码输入不一致')
          } else {
            axios.post('user/register', {
              userAccount: this.form.useraccount,
              userName: this.form.username,
              userPassword: this.form.userpassword
            }).then(response => {
              if (response.data.code === 200) {
                // 注册成功
                this.$message.success('注册成功！');
                this.$router.push('/LoginView')
              } else {
                // 注册失败，显示错误信息
                this.$message.error(response.data.msg);
              }
            }).catch(error => {
              console.error('请求错误:', error);
              // 处理请求错误
              this.$message.error('网络错误，请稍后重试');
            });
          }
        }
      })
    },
    goToLogin () {
      // 跳转到登录页面的逻辑
      this.$router.push('/LoginView')
    }
  }
}
</script>
    <style scoped>
    .register-container {
      max-width: 420px;
      margin: 60px auto;
      padding: 24px;
      border-radius: 8px;
      background-color: #f9f9f9;
      box-shadow: 0 6px 18px rgba(0,0,0,0.08);
    }

    .logo {
        width: 100px;
        height: auto;
        display: block;
        margin: 15px auto 20px;
      /* 调整徽标位置 */
    }

    #building {
      background: url("../assets/backgroud.jpg") center center / cover no-repeat;
      width: 100%;
      min-height: 100vh;
      position: relative;
    }

    .head {
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 0 16px 0;
    }

    .register-form {
        margin-bottom: 20px;
    }

    .return {
        float:left;
    }

</style>
