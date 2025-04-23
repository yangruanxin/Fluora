<template>
    <div class="register-body">
      <div class="register-form">
          <div class="login-image">
              <img src="/src/assets/图标.jpeg" alt="登录背景图">
          </div>
          <el-form
              ref="ruleFormRef"
              :model="ruleForm"
              :rules="rules"
              label-width="auto"
          >
              <h2>开启你的旅程吧！</h2>
              <!-- 账号框 -->
              <el-form-item label="账号" prop="account">
                  <el-input  placeholder="请输入您的账号" v-model="RegisterForm.account"/>
              </el-form-item>
              <!-- 密码框 -->
              <el-form-item label="密码" prop="password">
                  <el-input  placeholder="请输入您的密码" type="password" v-model="RegisterForm.password" show-password/>
              </el-form-item>
              <!-- 确认密码框 -->
              <el-form-item label="密码" prop="password">
                  <el-input  placeholder="请再次输入您的密码以确认" type="password" v-model="RegisterForm.password" show-password/>
              </el-form-item>
              <el-form-item label="">
                  <el-button type="primary">注册</el-button>
              </el-form-item>
          </el-form>
      </div>
    </div>
  </template>
  
  <script setup>
  
  import {reactive,ref} from "vue"
  
  const RegisterForm = reactive({
    account: '',
    password: ''
  })
  
  </script>
  
  <style lang="css" scoped>
  
  .register-body:before{
      content: "";
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-image:url("/src/assets/背景1.jpeg") ;
      background-size: cover;
      background-position: center;/* 居中 */
      filter: brightness(70%); /* 调整亮度 */
      z-index: -1; /* 让背景在最底层 */
  }
  
  .register-body{
      display: grid;
      place-items: center;
      min-height: 100vh;
  }
  
  .register-form{
      display: grid;
      grid-template-columns: 1fr 1fr; /* 两列等宽布局 */
      align-items: center; /* 垂直居中 */
      gap: 40px; /* 图片和表单之间的间距 */
      background-color: rgba(255, 255, 255, 0.8);
      background-position: center;
      padding: 8rem;
      border-radius: 20px;
      width: 500px;
  }
  
  .register-image {
      display: flex;
      justify-content: center;
  }
  
  .login-image img {
      max-width: 100%;
      max-height: 600px;
      object-fit: contain;
      border-radius: 8px;
  }
  
  .el-button{
      color:white;
      background-color: #0aa3fb;
      border-radius: 8px;
      width: 100%;
  }
  
  .el-input{
      border-radius: 8px;
  }
  
  </style>
  