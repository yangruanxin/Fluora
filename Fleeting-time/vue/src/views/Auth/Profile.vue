<!-- 个人主页 -->
<template>
    <div class="profile-page">
      <!-- 左侧信息栏 -->
      <div class="sidebar">
        <!-- 头像 -->
        <div class="avatar" 
        @click="triggerFileInput"
        :style="{ backgroundImage: `url(${user.avatarUrl || '/default-avatar.png'})`, backgroundSize: 'cover', backgroundPosition: 'center' }"
        ></div>
        <!-- 点击头像区域可上传头像 -->
        <input type="file" ref="fileInput" @change="handleAvatarUpload" style="display: none;" accept="image/*" />
        <!-- 用户名 -->
        <h2 class="username">{{ user.username }}</h2>
        <!-- 个性签名 -->
        <p class="bio">{{ user.bio }}</p>
        <!-- 旅行状态 -->
        <div class="stats">
          <!-- 旅行过多少地方 -->
          <div>
            <strong>{{user.places}}</strong>
            <p>places</p>
          </div>
          <!-- 累计打卡次数 -->
          <div>
            <strong>{{ user.times }}</strong>
            <p>times</p>
          </div>
        </div>
        <!-- 编辑资料 -->
        <button class="edit-button" @click="startEditing">编辑资料</button>
        <button class="logout-button" @click="showLogoutDialog">注销账号</button>
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
            </div>

            <!-- 时间线部分 -->
            <div ref="timelineSection" class="section timeline-section">
                <h3>Timeline Area</h3>
                <TimeLine />
            </div>
        </div>
      </div>
    </div>

    <!-- 弹窗：编辑资料 -->
    <el-dialog v-model="isEditDialogVisible" title="编辑资料" width="400px">
      <el-form label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="个性签名">
          <el-input v-model="editForm.bio" type="textarea" placeholder="请输入签名" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="isEditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProfile">保存</el-button>
      </template>
    </el-dialog>

    <!-- 弹窗：确认注销 -->
    <el-dialog v-model="isLogoutDialogVisible" title="确认注销账号" width="400px">
      <p>您确定要注销账号吗？此操作不可逆！</p>
      <template #footer>
        <el-button @click="isLogoutDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCancel">确认注销</el-button>
      </template>
    </el-dialog>

  </template>
  
  <script setup>
  import HoverButton from '@/components/HoverButton.vue'
  import TimeLine from '@/components/TimeLine.vue' 
  
  import {useRouter} from 'vue-router'
  import { ref, onMounted, onUnmounted } from 'vue'
  import { authAxios } from '@/utils/request'
  import { useAuthStore } from '@/stores/auth'
  import { ElMessage, ElMessageBox } from 'element-plus'


  // 编辑资料弹窗默认隐藏
  const isEditDialogVisible = ref(false)
  // 注销弹窗默认隐藏
  const isLogoutDialogVisible = ref(false); 

  const editForm = ref({
    username: '',
    bio: ''
  })

  const user = ref({
    username: '',
    bio: '',
    places: 0,
    times: 0,
    avatarUrl: ''
  })

  const activeTab = ref('map')
  const mapSection = ref(null)
  const timelineSection = ref(null)
  const router =useRouter()
  const fileInput = ref(null)


  // 当点击头像时触发文件选择框
  const triggerFileInput = () => {
    fileInput.value.click() 
  }

  // 处理头像上传按钮点击事件
  const handleAvatarUpload = async (event) => {
    const file = event.target.files[0]
    if (!file) return
    console.log('上传文件：', file.name, file.type, file.size)

    const formData = new FormData()
    formData.append('avatarFile', file)

    try {
      const response = await authAxios.put('/user/avatar', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      })
      console.log('上传返回结果：', response)
      user.value.avatarUrl = response.data.data // 后端返回上传后的头像 URL
    } catch (err) {
        console.error('上传头像失败：', err)
      }
  }

  // 处理编辑资料按钮点击事件
  const startEditing = () => {
    editForm.value.username = user.value.username
    editForm.value.bio = user.value.bio
    isEditDialogVisible.value = true
  }

  // 保存用户信息
  const saveProfile = async () => {
    try {
      const response = await authAxios.put('/user/info', {
        username: editForm.value.username,
        bio: editForm.value.bio
      })
      user.value.username = editForm.value.username
      user.value.bio = editForm.value.bio
      ElMessage.success('资料更新成功')
      isEditDialogVisible.value = false
    } catch (error) {
      ElMessage.error('更新失败，请重试')
    }
  }

  // 处理注销账号按钮点击事件
  const showLogoutDialog = () => {
    isLogoutDialogVisible.value = true;
  };

  // 注销操作
  const handleCancel = async () => {
    try {
      // 调用注销接口，执行注销逻辑
      await authAxios.delete('/user');
      console.log("注销成功");
      // 注销成功后，清除本地存储的token
      const authStore=useAuthStore()
      authStore.logout()

      // 注销成功后，跳转到首页
      router.push('/'); 

      // 关闭弹窗
      isLogoutDialogVisible.value = false;
    } catch (err) {
      console.error("注销失败:", err);
    }
  };

  onMounted(async () => {
    const authStore = useAuthStore()
    console.log(authStore.token)
    try {
      const response = await authAxios.get('/user/me')
      console.log('拿到的用户数据', response.data)
      console.log(response.data.data)

      // 把后端返回的数据填充到 user
      user.value.username = response.data.data.username ?? '游客'
      user.value.bio = response.data.data.bio ?? 'This is a bio...'
      user.value.places = response.data.data.places ?? 0
      user.value.times = response.data.data.times ?? 0
      user.value.avatarUrl = response.data.data.avatarUrl ?? ''

      console.log(user)

    } catch (error) {
      console.error('获取个人信息失败：', error)
    }
  })
  
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
  
  /* 头像 */
  .avatar {
    width: 120px;
    height: 120px;
    background-color: #ddd;
    border-radius: 50%;
    margin: 0 auto 16px;
  }
  
  /* 用户名 */
  .username {
    text-align: center;
    margin: 8px 0;
    font-size: 20px;
  }
  
  /* 个性签名 */
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
  
  .edit-button,.logout-button {
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
  
  .logout-button:hover,
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

  .el-input{
    border-radius: 8px;
  }
  </style>