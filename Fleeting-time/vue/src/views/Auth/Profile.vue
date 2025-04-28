<!-- 个人主页 -->
<template>
    <div class="profile-page">
      <!-- 左侧信息栏 -->
      <div class="sidebar">
        <!-- 头像 -->
        <div class="avatar"></div>
        <!-- 用户名 -->
        <h2 class="username">Username</h2>
        <!-- 个性签名 -->
        <p class="bio">This is a bio...</p>
        <!-- 旅行状态 -->
        <div class="stats">
          <!-- 旅行过多少地方 -->
          <div>
            <strong>20</strong>
            <p>places</p>
          </div>
          <!-- 累计打卡次数 -->
          <div>
            <strong>50</strong>
            <p>times</p>
          </div>
        </div>
        <!-- 编辑资料 -->
        <button class="edit-button">Edit Profile</button>
      </div>
  
      <!-- 右侧主展示区 -->
      <div class="main-area">
        <!-- 顶部标签 -->
         <div class="top">
            <div class="tabs">
                <button :class="{ active: activeTab === 'map' }" @click="scrollTo('map')">Map</button>
                <button :class="{ active: activeTab === 'timeline' }" @click="scrollTo('timeline')">Timeline</button>
            </div>

            <HoverButton text="返回首页" @click="handleBack"/>
         </div>

  
        <!-- 内容区 -->
        <div class="content">
            <!-- 地图部分 -->
            <div ref="mapSection" class="section map-section">
                <h3>Map Area</h3>
                <p>Here is the map (to be implemented)</p>
            </div>

            <!-- 时间线部分 -->
            <div ref="timelineSection" class="section timeline-section">
                <h3>Timeline Area</h3>
                <TimeLine />
            </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import HoverButton from '@/components/HoverButton.vue'
  import {useRouter} from 'vue-router'
  import { ref, onMounted, onUnmounted } from 'vue'
  import TimeLine from '@/components/TimeLine.vue' 
  
  const activeTab = ref('map')
  const mapSection = ref(null)
  const timelineSection = ref(null)
  const router =useRouter()
  
  const scrollTo = (section) => {
    if (section === 'map' && mapSection.value) {
      mapSection.value.scrollIntoView({ behavior: 'smooth' })
    }
    if (section === 'timeline' && timelineSection.value) {
      timelineSection.value.scrollIntoView({ behavior: 'smooth' })
    }
  }
  
  // 根据滚动位置，动态更新 activeTab
  const handleScroll = () => {
    const mapTop = mapSection.value?.getBoundingClientRect().top || 0
    const timelineTop = timelineSection.value?.getBoundingClientRect().top || 0
  
    if (timelineTop < window.innerHeight / 2) {
      activeTab.value = 'timeline'
    } else {
      activeTab.value = 'map'
    }
  }
  
  onMounted(() => {
    window.addEventListener('scroll', handleScroll)
  })
  
  onUnmounted(() => {
    window.removeEventListener('scroll', handleScroll)
  })

  const handleBack = () =>{
    router.push('/')
  }
  </script>
  
  <style lang="css" scoped>
  .profile-page {
    display: flex;
    min-height: 100vh;
    background-image: url("/src/assets/背景3.jpeg");
    background-size: cover;
    background-position: center;
    z-index: -1;
  }
  
  /* 左侧 */
  .sidebar {
    width: 260px;
    background-color: #ffffffa0;
    padding: 24px;
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
    position: sticky;
    top: 0;
    height: 100vh;
  }
  
  .avatar {
    width: 120px;
    height: 120px;
    background-color: #ddd;
    border-radius: 50%;
    margin: 0 auto 16px;
  }
  
  .username {
    text-align: center;
    margin: 8px 0;
    font-size: 20px;
  }
  
  .bio {
    text-align: center;
    font-size: 14px;
    color: #777;
  }
  
  .stats {
    display: flex;
    justify-content: space-around;
    margin: 20px 0;
  }
  
  .stats div {
    text-align: center;
  }
  
  .edit-button {
    display: block;
    width: 100%;
    padding: 8px;
    background-color: #4f46e5;
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    margin-top: 10px;
  }
  
  .edit-button:hover {
    background-color: #4338ca;
  }
  
  /* 右侧主区域 */
  .main-area {
    flex: 1;
    padding: 24px;
  }

  .top{
    display: flex;
    justify-content:space-between;
    align-items: center;
    margin-bottom: 16px;
    position: sticky;
    top: 0;
    z-index: 100;
  }
  
  .tabs {
    display: flex;
    gap: 16px;
    margin-bottom: 24px;
  }
  
  .tabs button {
    background: none;
    border: none;
    font-size: 16px;
    padding: 8px 16px;
    cursor: pointer;
    color: #666;
    border-bottom: 2px solid transparent;
  }
  
  .tabs button.active {
    color: #333;
    border-color: #4f46e5;
  }
  
  .section {
    background: white;
    margin-bottom: 60px;
    padding: 40px 20px;
    border-radius: 8px;
    min-height: 600px;
  }
  
  </style>