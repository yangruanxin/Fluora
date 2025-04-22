<template>
  <div class="layout">
    <el-container>
      <el-header class="header">
        <div class="logo">
          <img src="/src/assets/图标.jpeg" alt="logo" class="logo-icon" />
          <span>流光</span>
        </div>
        <div class="header-links">
          <HoverButton text="登录" />
          <HoverButton text="注册" />
          <HoverButton text="我的" />
        </div>
      </el-header>
      <el-main class="main">
      </el-main>
      <el-footer class="footer">
      </el-footer>

    <!-- 雪花特效 -->
    <Snow
      class="snow-overlay"
      :quantity="150"
      color="#ffffff"
      :speed="1.5"
      :maxRadius="4"
      :minRadius="1"
    />
    </el-container>
  </div>
</template>

<script setup lang="ts">
import HoverButton from '@/components/HoverButton.vue';
import Snow from '@/components/Snow.vue';
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
  filter: brightness(90%);
  z-index: -1; /* 让背景在最底层 */
}

.layout{
  min-height: 100%;
  display: flex; /* 容器为flex布局 */
  flex-direction: column; /* 方向为纵向，保持正常的从上到下文档流 */
}

/* 导航栏 */
.header {
  display: flex;
  justify-content: space-between; /* logo 左边，按钮组右边 */
  align-items: center;            /* 垂直居中 */
  padding: 0 20px;
  height: 70px;
  border: none;
}


.logo{
  font-size: 24px;
  font-weight: bold;
  display: flex;
}

/* logo图标 */
.logo-icon {
  width: 40px;
  height: 40px;
  margin-right: 8px;
  border-radius: 8px;
}

/* 导航栏按钮 */
.header-links {
  display:flex;
  gap: 16px; /* 按钮间距 */
}

/* 底部信息栏 */
footer {
  position: absolute;
  bottom: 0; 
  width: 100%;
}

/* 降雪背景动画 */
.snow-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none; /* 不影响点击 */
  z-index: 999;
}

</style>