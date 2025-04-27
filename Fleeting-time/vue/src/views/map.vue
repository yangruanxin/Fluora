<!-- vue3引入百度api -->
<template>
  <div class="map-container">
    <div class="header-row">
      <el-button @click="resetMap" class="reset-map">重置地图</el-button> 
      <h2>点击地图，选择你的旅行目的地</h2>
      <HoverButton text="返回" @click="handleBack" class="back"/>
    </div>
    <div id="allmap"></div>
  </div>

  <!-- 上传旅行记录浮窗 -->
  <div v-if="showModal" class="modal-overlay">
    <div class="modal">
      <h3>添加旅行记录</h3>
      <p>目的地：{{ locationInfo.name }}</p>
      <FileSubmit class="additional-class">
      </FileSubmit>
      <textarea v-model="description" placeholder="记录一下你的感受吧..." rows="4"></textarea>
      <div class="buttons">
        <el-button type="primary" @click="submit">提交</el-button>
        <el-button type="primary" @click="closeModal">取消</el-button>
      </div>
    </div>
  </div>

</template>

<script setup>
import HoverButton from "@/components/HoverButton.vue";
import FileSubmit from "@/components/FileSubmit.vue";
import {useRouter} from "vue-router";
import { ref,onMounted } from "vue";

const router=useRouter();

const showModal = ref(false) // 控制浮窗显示
const locationInfo = ref({}) // 保存选中的地点信息（名字、经纬度）
const uploadImage = ref(null) // 保存上传的图片
const description = ref('') // 保存填写的描述
let map=null

//处理返回首页按钮点击事件
const handleBack = () => {
  router.push('/')
}

onMounted(() => {
  console.log(document.getElementById("allmap")); 
  loadMapScript(); // 加载百度地图资源
});


// 初始化地图
const init = () => {
  let Bmap = window.BMap; 
  map = new Bmap.Map("allmap"); // allmap必须和dom上的id一直
  map.centerAndZoom(
    new Bmap.Point(108.948024, 34.263161),
    5
  ); // 初始化地图,设置中心点坐标和地图级别
  map.setCurrentCity("陕西省咸阳市泾阳县永乐镇北流村");
  map.enableScrollWheelZoom(true);

  // 处理地图点击事件
  console.log('准备绑定点击事件');
  map.addEventListener('click', function(e) {
    console.log('点击了地图', e.point);
    const lng = e.point.lng
    const lat = e.point.lat
    fetch(`http://121.43.136.251:8080/api/map/reverse-geocode?lat=${lat}&lng=${lng}`)
    .then(res => {
      return res.text(); // 获取响应的文本内容
    })
    .then(text => {
    console.log(text); // 查看返回的文本内容
    try {
      const data = JSON.parse(text); // 尝试解析为JSON
      if (data.status === 0) {
        const address = data.result.addressComponent;
        let name = '';

        if (address.country === '中国') {
          name = `${address.province} ${address.city}`;//中国省市
        } else {
          name = `${address.country} ${address.city || ''}`.trim(); //国家+城市
        }

        locationInfo.value = {
          name,
          lng,
          lat
        };
        showModal.value = true;
      } else {
        alert('逆地理编码失败');
      }
    } catch (e) {
      console.error('无法解析返回的数据为JSON', e);
    }
  })
  .catch((err) => {
    console.error(err);
    alert('请求失败');
  });

  })
};


const loadMapScript = () => {
  var script = document.createElement("script");
  script.type = "text/javascript";
  script.className = "loadmap"; // 给script一个类名
  script.src =
    "https://api.map.baidu.com/getscript?v=3.0&ak=OUxgi9tGCKkijW4VW8d5F8FxcFRNfDfz";
  script.onload = () => {
    console.log("百度地图脚本加载成功");
    // 使用script.onload，待资源加载完成，再初始化地图
    init();
  };
  let loadmap = document.getElementsByClassName("loadmap");
  if (loadmap) {
    // 每次append script之前判断一下，避免重复添加script资源标签
    for (var i = 0; i < loadmap.length; i++) {
      document.body.removeChild(loadmap[i]);
    }
  }
  document.body.appendChild(script);
};

const handleImageUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    uploadImage.value = file
  }
}

const submit = () => {
  console.log('提交内容：', {
    location: locationInfo.value,
    image: uploadImage.value,
    description: description.value
  })
  closeModal()
}

const closeModal = () => {
  showModal.value = false
  locationInfo.value = {}
  uploadImage.value = null
  description.value = ''
}

//重置地图
const resetMap = () => {
  let Bmap = window.BMap;
  // 重置为初始的中心点和缩放级别
  map.centerAndZoom(new Bmap.Point(108.948024, 34.263161), 5); 
  map.clearOverlays(); // 可选：清除任何已添加的标记或覆盖物
};

</script>

<style scoped>
/* 去除水印 */
::v-deep(.BMap_cpyCtrl) {
  display: none;
}
 
::v-deep(.anchorBL) {
  display: none !important;
}
 
.map-container {
  display: flex;
  flex-direction: column;
  height: 100vh; /* 父容器明确高度 */
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px; /* 添加与地图的间距 */
}

h2 {
  padding: 16px;
  text-align: center;
  font-weight: bold;
  font-size: 20px;
  margin: 0; /* 去除默认margin */
}

#allmap {
  flex: 1; /* 占据剩余空间 */
  width: 100%;
  position: relative;
  z-index: 10;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5); /* 半透明背景 */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000; /* 必须高过地图 */
}

.modal {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  z-index: 1001; /* 更高，确保弹窗内容在最上面 */
}

.reset-map {
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 10px;
}

.reset-map:hover {
  background-color: #0056b3;
}

</style>