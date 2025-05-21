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
              <div class="header-row">
                <h2>我的旅行地图</h2>
              </div>
              <div id="allmap"></div>
            </div>

            <div ref="timelineSection" class="section timeline-section">
              <div class="header-row mb-6">
                <h2 class="text-2xl font-bold">我的旅行时间线</h2>
              </div>
              <div class="w-full ">
                <TimeLine :items="posts">
                  <template v-for="(item) in posts" :key="item.id" #[item.id]>
                    <div
                    class="timeline-item flex flex-col md:flex-row items-start gap-4 p-20 rounded-lg shadow-md bg-white dark:bg-neutral-800 mb-6 transition hover:scale-[1.01] -ml-30 md:-ml-64 "
                    @click="openEditor(item)"
                  >
                    <!-- 时间 -->
                    <div class="md:w-1/5 text-neutral-500 text-sm font-medium">
                      <p>{{ formatDate(item.beginTime) }}</p>
                      <p class="text-center">-</p>
                      <p>{{ formatDate(item.endTime) }}</p>
                    </div>

                    <!-- 图片 -->
                    <div class="md:w-1/4 w-full flex justify-center md:justify-start">
                      <img
                        v-if="item.imageUrls.length > 0"
                        :src="item.imageUrls[0]"
                        class="w-32 h-32 object-cover rounded-lg border"
                      />
                    </div>

                    <!-- 内容 -->
                    <div class="md:w-3/5 w-full">
                      <h3 class="text-lg font-semibold text-neutral-700 dark:text-neutral-200">
                        {{ item.locationName }}
                      </h3>
                      <p class="mt-1 text-sm text-neutral-600 dark:text-neutral-400 whitespace-pre-line">
                        {{ item.content }}
                      </p>
                    </div>
                  </div>

                  </template>
                </TimeLine>
              </div>
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

    <!-- 弹窗：编辑旅行记录 -->
    <el-dialog v-model="editPostDialogVisible" title="编辑打卡内容" width="600px">
      <el-form label-position="top">
        <!-- 图片预览与切换 -->
        <el-form-item label="图片预览">
          <div class="image-preview-wrapper">
            <el-button :icon="ArrowUp" circle @click="prevImage" :disabled="currentImageIndex === 0" />
            <img
              :src="editingPost.imageUrls[currentImageIndex]"
              alt="预览图"
              class="image-preview"
            />
            <el-button :icon="ArrowDown" circle @click="nextImage" :disabled="currentImageIndex >= editingPost.imageUrls.length - 1" />
          </div>

          <!-- 操作按钮 -->
          <div class="image-ops">
            <el-upload
              action="/upload"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
            >
              <el-button type="primary" icon="Plus">添加图片</el-button>
            </el-upload>
            <el-popconfirm title="确定删除当前图片？" @confirm="deleteCurrentImage">
              <template #reference>
                <el-button type="danger" icon="Delete" :disabled="editingPost.imageUrls.length <= 1">删除当前图片</el-button>
              </template>
            </el-popconfirm>
          </div>
        </el-form-item>

        <!-- 描述 -->
        <el-form-item label="描述">
          <el-input
            v-model="editingPost.content"
            type="textarea"
            :rows="4"
            placeholder="请输入打卡描述"
          />
        </el-form-item>
      </el-form>

      <!-- 底部按钮 -->
      <template #footer>
        <el-button @click="editPostDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEditedPost">保存</el-button>
      </template>
    </el-dialog>



  </template>
  
  <script setup>
  import HoverButton from '@/components/HoverButton.vue'
  import TimeLine from '@/components/TimeLine.vue' 
  import ProvinceImage from '@/components/ProvinceImageMask.vue' 

  
  import {useRouter} from 'vue-router'
  import { ref, onMounted, onUnmounted } from 'vue'
  import { authAxios } from '@/utils/request'
  import {
    createProvinceMaskOverlay
  } from '@/utils/provinceMaskUtils'
  import { useAuthStore } from '@/stores/auth'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { ArrowUp, ArrowDown } from '@element-plus/icons-vue'



  let map = null;

  // 初始化地图
  const init = () => {
    let Bmap = window.BMap;
    map = new Bmap.Map("allmap"); 
    map.centerAndZoom(
      new Bmap.Point(108.948024, 34.263161), // 设置地图中心点坐标
      5 // 设置地图级别（缩放级别）
    );
    map.setCurrentCity("陕西省咸阳市泾阳县永乐镇北流村");

    // 禁用滚轮缩放
    map.disableScrollWheelZoom();

    // 禁用双击放大
    map.disableDoubleClickZoom();

    // 添加图片覆盖物
    addImageOverlays();
  };

  const addImageOverlays = () => {
    if (!posts.value || posts.value.length === 0) return;

    const latestPostsByLocation = {};
    posts.value.forEach(post => {
      if (!latestPostsByLocation[post.locationName] || 
          new Date(post.beginTime) > new Date(latestPostsByLocation[post.locationName].beginTime)) {
        latestPostsByLocation[post.locationName] = post;
      }
    });

    const Bmap = window.BMap;

    Object.values(latestPostsByLocation).forEach(post => {
      if (post.imageUrls?.length > 0 && post.latitude && post.longitude) {
        const point = new Bmap.Point(post.longitude, post.latitude);
        const imageUrl = post.imageUrls[0];

        // 调用 province 遮罩构造函数（以 post.locationName 为省名）
        createProvinceMaskOverlay(post.locationName, imageUrl, map, Bmap)
          .then((overlay) => {
            overlay.addEventListener("click", () => {
              openEditor(post);
            });
            map.addOverlay(overlay);
          })
          .catch((err) => {
            console.warn("创建遮罩失败：", err);
          });
      }
    });
  };

  // 加载地图脚本
  const loadMapScript = () => {
    var script = document.createElement("script");
    script.type = "text/javascript";
    script.className = "loadmap";
    script.src =
      "https://api.map.baidu.com/getscript?v=3.0&ak=OUxgi9tGCKkijW4VW8d5F8FxcFRNfDfz";
    script.onload = () => {
      init(); // 脚本加载完成后初始化地图
      // 如果已经有数据，添加覆盖物
      if (posts.value && posts.value.length > 0) {
        addImageOverlays();
      }
    };
    let loadmap = document.getElementsByClassName("loadmap");
    if (loadmap) {
      for (var i = 0; i < loadmap.length; i++) {
        document.body.removeChild(loadmap[i]);
      }
    }
    document.body.appendChild(script);
  };

  const timelineItems = ref([]);

  // 当页面加载完成时，初始化地图
  onMounted(() => {
    loadMapScript();
  });

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
    avatarUrl: '',
    checkins:[]
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

  const posts = ref([]);

  // 加载用户数据
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

      try {
        const response = await authAxios.get('/travel-posts/me');
        if (response.data.code === 200) {
          posts.value = response.data.data;
          console.log(posts.value);
          posts.value.sort((a, b) => new Date(b.beginTime) - new Date(a.beginTime));

          // 确保地图已经初始化后再添加覆盖物
          if (map) {
            addImageOverlays();
          }
        } else {
          console.error('获取游记失败：', response.data.message);
        }
      } catch (err) {
        console.error('请求游记接口出错：', err);
      }
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

  // 处理返回按钮点击事件
  const handleBack = () =>{
    router.push('/')
  }

  // 格式化日期的函数
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = date.getMonth() + 1; // 月份从0开始，+1是为了正确显示
    const day = date.getDate();
    return `${year}.${month}.${day}`; // 拼接成 'YYYY.MM.DD' 格式
  };

  // 点击卡片编辑卡片信息
  const editPostDialogVisible = ref(false)
  const editingPost = ref({
    postId: '',
    title: '',
    imageUrls:[],
    content: '',
    locationName: '',
    latitude: '',
    longitude: '',
    beginTime: '',
    endTime: ''
  });
  const currentImageIndex = ref(0)

  const openEditor = (item) => {
    editingPost.value = {
      postId: item.id,
      title:item.title,
      imageUrls: item.imageUrls,  
      content: item.content,
      locationName: item.locationName,
      latitude: item.latitude,
      longitude: item.longitude,
      beginTime: item.beginTime,
      endTime: item.endTime
    };
    console.log(editingPost.value);
    editPostDialogVisible.value = true;
  };

  const deleteCurrentImage = () => {
    editingPost.value.imageUrls.splice(currentImageIndex.value, 1)
    // 防止 currentImageIndex 越界
    if (currentImageIndex.value >= editingPost.value.imageUrls.length) {
      currentImageIndex.value = editingPost.value.imageUrls.length - 1
    }
  }


  // 上一张图片
  function prevImage() {
    if (currentImageIndex.value > 0) {
      currentImageIndex.value--
    }
  }

  // 下一张图片
  function nextImage() {
    if (currentImageIndex.value < editingPost.value.imageUrls.length - 1) {
      currentImageIndex.value++
    }
  }

  const saveEditedPost = async () => {
    try {
      if (!editingPost.value || !editingPost.value.postId) {
        console.error('无效的帖子数据');
        return;
      }

      const formData = new FormData();
      formData.append('title', editingPost.value.title);
      formData.append('content', editingPost.value.content);
      formData.append('locationName', editingPost.value.locationName);
      formData.append('latitude', editingPost.value.latitude);
      formData.append('longitude', editingPost.value.longitude);
      formData.append('beginTime', formatDate(editingPost.value.beginTime));
      formData.append('endTime', formatDate(editingPost.value.endTime));

      const response = await authAxios.put(
        `/travel-posts/${editingPost.value.postId}`,
        formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });

      if (response.data.code === 200) {
        console.log('保存成功：', response.data.data);
        ElMessage.success('保存成功');
        editPostDialogVisible.value = false;
      } else {
        console.error('保存失败：', response.data.message);
        ElMessage.error('保存失败，请检查输入内容');
      }
    } catch (err) {
      console.error('请求保存接口出错：', err);
      ElMessage.error('请求出错，请稍后重试');
    }
  };


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
    padding: 40px;
    border-radius: 8px;
    min-height: 600px;
  }

  .map-section{
    display: flex;
    flex-direction: column;
    height: 100%; /* 父容器高度 */
  }

  #allmap {
    width: 100%;  /* 确保地图区域宽度填满父容器 */
    height: 400px; /* 设置地图区域的固定高度 */
  }

  .header-row {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 16px; /* 增加与地图的间距 */
  }

  h2 {
    font-size: 20px;
    text-align: center;
    font-weight: bold;
  }

  /* 去除水印 */
  ::v-deep(.BMap_cpyCtrl) {
    display: none;
  }
  
  ::v-deep(.anchorBL) {
    display: none !important;
  }

  .el-input{
    border-radius: 8px;
  }
  </style>