<template>
  <div class="layout">
    <el-container>
      <el-header class="header">
        <div class="logo">流光</div>
        <div class="header-links">
          <HoverButton text="登录" />
          <HoverButton text="注册" />
          <HoverButton text="我的" />
        </div>
      </el-header>
      <el-main class="main">Main</el-main>
      <el-footer class="footer">Footer</el-footer>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import HoverButton from '../components/HoverButton.vue';
</script>

<style lang="css" scoped>
.layout:before{
  content: "";
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:url("/src/assets/背景2.jpeg") ;
  background-size: cover;
  background-position: center;/* 居中 */
  z-index: -1; /* 让背景在最底层 */
}

.layout{
  min-height: 100%;
  display: flex; /* 容器为flex布局 */
  flex-direction: column; /* 方向为纵向，保持正常的从上到下文档流 */
}

.header {
  display: flex;
  justify-content: space-between; /* logo 左边，按钮组右边 */
  align-items: center;            /* 垂直居中 */
  padding: 0 20px;
  height: 60px;
  background-color: #fff;
  border-bottom: 1px solid #ddd;
}


.logo{
  font-size: 24px;
  font-weight: bold;
}

.header-links {
  gap: 16px; /* 按钮间距 */
}

footer {
  position: absolute;
  bottom: 0; 
  width: 100%;
  background-color: #457B9D;
}

</style>