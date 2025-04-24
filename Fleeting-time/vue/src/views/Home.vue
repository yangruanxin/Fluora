<template>
  <div class="layout">
    <el-container>
      <!-- 导航栏 -->
      <el-header class="header">
        <div class="logo">
          <img src="/src/assets/图标.jpeg" alt="logo" class="logo-icon" />
          <span>流光</span>
        </div>
        <div class="header-links">
          <HoverButton text="登录" @click="handleLogin"/>
          <HoverButton text="注册" @click="handleRegister"/>
          <HoverButton text="我的" />
        </div>
      </el-header>
      <!-- 主内容 -->
      <el-main class="main">
        <!-- 垂直布局容器 -->
        <div class="vertical-layout">
          <!-- Hover组件 -->
          <div class="hover-section">
            <Hover imageUrl="/src/assets/地图.png">
              <p>探索更多旅程</p>
              <HoverButton text="点击查看地图" @click="handleMap"/>
            </Hover>
          </div>
          
          <!-- 图片轮播 -->
          <div class="testimonial-wrapper relative">
            <!-- 遮罩层 -->
            <div class="absolute inset-0 bg-white/60 dark:bg-black/40 backdrop-blur-sm z-0 rounded-3xl"></div>
            
            <!-- 轮播组件内容 -->
            <div class="relative z-10 text-left">
              <AnimatedTestimonials :testimonials="testimonials" :autoplay="true" :duration="6000" />
            </div>
          </div>
        </div>
      </el-main>
      <!-- 页脚 -->
      <el-footer class="footer">
        <div class="footer-content">
          <!-- 版权信息 -->
          <div class="copyright">
            © 2025 流光 | 记录每一段旅程的故事
          </div>
        </div>
      </el-footer>

    <!-- 雪花特效 -->
    <Snow
      class="snow-overlay"
      :quantity="150"
      color="#ffffff"
      :speed="0.5"
      :maxRadius="4"
      :minRadius="1"
    />
    </el-container>
  </div>
</template>

<script setup lang="ts">
import {useRouter} from 'vue-router';
import HoverButton from '@/components/HoverButton.vue';
import Snow from '@/components/Snow.vue';
import AnimatedTestimonials from '@/components/AnimatedTestimonials.vue';
import Hover from '@/components/Hover.vue';

//导入路由实例
const router =useRouter()

//处理登录按钮点击事件
const handleLogin = () =>{
  router.push('/login')
}

//处理注册按钮点击事件
const handleRegister = () =>{
  router.push('/register')
}

//处理查看地图按钮点击事件
const handleMap = () =>{
  router.push('/map')
}


//图片轮播
const testimonials = [
  {
    name: '中国四川',
    designation: '贡嘎雪山',
    quote: '当你真正站在凛冽的风中，看着雪山一寸寸亮成火炬——你会相信：有些美，本就是宇宙级的浪漫。',
    image: '/src/assets/风景图/贡嘎雪山.png',
  },
  {
    name: '挪威特罗姆瑟',
    designation: '极光',
    quote: '在北欧神话中，极光被认为是天空女神雅芬的裙摆，象征着生命的活力和热情。去做世界尽头的追光者。',
    image: '/src/assets/风景图/挪威极光.png',
  },
  {
    name: '法国巴黎',
    designation: '埃菲尔铁塔',
    quote: '铁塔是凝固的星群，而我们在其间流动。',
    image: '/src/assets/风景图/法国巴黎.png',
  },
];

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

/* 主内容 */
.main {
  padding: 60px 20px;
  display: flex;
  flex: 1; 
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

/* 垂直布局容器 */
.vertical-layout {
  display: flex;
  flex-direction: column;
  gap: 40px;
  width: 100%;
  max-width: 1200px;
}

/* Hover组件调整 */
.hover-section {
  display: flex;
  justify-content: flex-start;
  width: 100%;
  font-weight: bold;
}

p{
  font-size: 150%;
  font-weight: bold;
  margin-bottom: 20px;
  color:black;
}

/* 轮播组件调整 */
.testimonial-wrapper {
  width: 100%;
  margin-top: 20px;
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


/* 页脚 */
.footer {
  flex-shrink: 0;
  padding: 40px 0 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(7, 7, 7, 0.8);
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.copyright {
  font-size: 16px;
  opacity: 0.8;
}

</style>