<template>
  <div id="building">
    <img src="../assets/logo.png" class="logo" alt="logo"> <!-- 添加徽标 -->
    <div class="change-container">
      <el-button type="text" class="return" @click="goToLogin">返回</el-button>
      <div class="head">
        <h2>修改密码</h2>
      </div>
      <el-form :model="form" :rules="rules" ref="registerForm" class="register-form" label-width="80px">
        <el-form-item label="账号" prop="useraccount">
          <el-input v-model="form.useraccount" placeholder="请输入账号"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="form.password" placeholder="请输入密码" clearable
                    show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="form.confirmPassword" placeholder="请确认密码" clearable
                    show-password></el-input>
        </el-form-item>
        <el-button type="primary" style="width: 90%" @click="ChangePassword">修改密码</el-button>

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
        useraccount:'',
        password: '',
        confirmPassword:''
      },
      rules: {
        useraccount:[
          {
            required: true,
            message: '请输入账号',
            trigger: 'blur'
          }
        ],
        password: [
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
    ChangePassword () {
      this.$refs.registerForm.validate((valid) => {
        if (valid) {
          if (this.form.password !== this.form.confirmPassword) {
            this.$message.error('两次密码输入不一致')
          }
          else {
            const getUserAccount = this.form.useraccount
            const url = `user/list?userAccount=${getUserAccount}`;
            axios.get(url).then(response => {
                const user = response.data
                const userId = user[0].userId
                const oldPassword = user[0].userPassword
              if(this.form.password === oldPassword)
              {
                this.$message.error('新密码不能与旧密码相同')
              }
              else{
                const requestBody = {
                  userId: userId,
                  userPassword: this.form.password
                };
                axios.put('user/update', requestBody)
                  .then(response => {
                    if(response.data.code === 200)
                    {
                     this.$message.success('修改密码成功')
                    }
                  })
                  .catch(error => {
                    this.$message.error('修改密码失败')
                  });
              }

            })

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
.change-container {
  max-width: 350px;
  margin: 100px auto;
  padding: 20px;
  border-radius: 5px;
  background-color: #f9f9f9;
}

.logo {
  width: 100px;
  height: auto;
  display: block;
  margin: 15px auto 20px;
  /* 调整徽标位置 */
}

#building {
  background: url("../assets/backgroud.jpg");
  width: 100%;
  height: 100%;
  position: fixed;
  background-size: 100% 100%;
}

.head {
  display: flex;
  align-items: center;
  margin-left: 126px
}

.register-form {
  margin-bottom: 20px;
}

.return {
  float:left;
}

</style>

