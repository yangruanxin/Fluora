<!-- vue3引入百度api -->
<template>
  <div class="map-container">
    <div class="header-row">
      <h2>点击地图，选择你的旅行目的地</h2>
      <HoverButton text="返回" @click="handleBack" class="back"/>
    </div>
    <div id="allmap"></div>
  </div>
</template>

<script setup>
import HoverButton from "@/components/HoverButton.vue";
import {useRouter} from "vue-router";
import { onMounted } from "vue";

const router=useRouter();

const handleBack = () => {
  router.push('/')
}

onMounted(() => {
  loadMapScript(); // 加载百度地图资源
});
// 初始化地图
const init = () => {
  let Bmap = window.BMap; 
  var map = new Bmap.Map("allmap"); // allmap必须和dom上的id一直
  map.centerAndZoom(
    new Bmap.Point(108.948024, 34.263161),
    5
  ); // 初始化地图,设置中心点坐标和地图级别
  map.setCurrentCity("陕西省咸阳市泾阳县永乐镇北流村");
  map.enableScrollWheelZoom(true);
  map.addEventListener('click', function(e) {
  alert('点击位置经纬度：' + e.point.lng + ',' + e.point.lat);
  });
};
const loadMapScript = () => {
  var script = document.createElement("script");
  script.type = "text/javascript";
  script.className = "loadmap"; // 给script一个类名
  script.src =
    "https://api.map.baidu.com/getscript?v=3.0&ak=OUxgi9tGCKkijW4VW8d5F8FxcFRNfDfz";
  script.onload = () => {
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
}
</style>