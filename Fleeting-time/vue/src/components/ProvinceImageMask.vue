<template>
  <!-- 不需要可见 DOM -->
</template>

<script setup>
import { onMounted, onBeforeUnmount, watch } from 'vue'

const props = defineProps({
  province: { type: String, required: true }, // 省名，例如 "湖北省"
  imageUrl: { type: String, required: true }, // 需要裁切的图片地址
  map: { type: Object, required: true }, // 百度地图实例（BMap.Map）
})

let overlayInstance = null

onMounted(async () => {
  overlayInstance = await createProvinceMaskOverlay(props.map, props.province, props.imageUrl)
})

onBeforeUnmount(() => {
  if (overlayInstance && props.map) {
    props.map.removeOverlay(overlayInstance)
  }
})

watch(() => props.province, async (newProvince) => {
  if (overlayInstance && props.map) props.map.removeOverlay(overlayInstance)
  overlayInstance = await createProvinceMaskOverlay(props.map, newProvince, props.imageUrl)
})
</script>
