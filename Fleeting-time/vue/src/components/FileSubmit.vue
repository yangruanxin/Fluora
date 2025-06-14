<template>
  <ClientOnly>
    <div
      :class="cn('w-full', $props.class)"
      @dragover.prevent="handleEnter"
      @dragleave="handleLeave"
      @drop.prevent="handleDrop"
      @mouseover="handleEnter"
      @mouseleave="handleLeave"
    >
      <div
        class="group/file relative block w-full cursor-pointer overflow-hidden rounded-lg p-10"
        @click="handleClick"
      >
        <input
          ref="fileInputRef"
          type="file"
          class="hidden"
          @change="onFileChange"
          accept="image/*,application/pdf"
          multiple
        />

        <!-- Grid pattern -->
        <div
          class="pointer-events-none absolute inset-0 [mask-image:radial-gradient(ellipse_at_center,white,transparent)]"
        >
          <slot />
        </div>

        <!-- Content -->
        <div class="flex flex-col items-center justify-center">
          <p
            class="relative z-20 font-sans text-base font-bold text-neutral-700 dark:text-neutral-300"
          >
            图片上传
          </p>
          <p
            class="relative z-20 mt-2 font-sans text-base font-normal text-neutral-400 dark:text-neutral-400"
          >
            拖拽图片到此处或点击此处以上传
            <br>
            拖动 ☰ 可为图片排序
            <br>
            点击图片查看大图
            <br>
            点击 x 删除图片
          </p>

          <div
            class="relative mx-auto mt-10 w-full max-w-xl space-y-4 overflow-y-auto rounded-md"
            style="max-height: 300px;"
          >
          <draggable
            v-model="files"
            item-key="name"
            animation="200"
            ghost-class="drag-ghost"
            handle=".drag-handle"
            @update="onSortUpdate"
          >
            <template #item="{ element: file, index: idx }">
              <Motion
                :key="`file-${idx}`"
                :initial="{ opacity: 0, scaleX: 0 }"
                :animate="{ opacity: 1, scaleX: 1 }"
                class="relative z-40 mx-auto flex w-full flex-col items-start justify-start overflow-hidden rounded-md bg-white p-4 shadow-sm md:h-24 dark:bg-neutral-900"
              >
                <div class="flex w-full items-center gap-4">
                  <!-- 添加一个拖拽手柄 -->
                  <span class="drag-handle cursor-move text-xl">☰</span>

                  <Motion
                    :initial="{ opacity: 0 }"
                    :animate="{ opacity: 1 }"
                  >
                    <el-image
                      v-if="file.type.startsWith('image/')"
                      :src="file.previewUrl || URL.createObjectURL(file)"
                      :preview-src-list="[file.previewUrl || URL.createObjectURL(file)]"
                      :preview-teleported="true"
                      class="h-20 w-20 rounded-md object-cover cursor-pointer"
                      fit="cover"
                      @click.stop
                    />
                    <!-- 删除按钮 -->
                    <el-icon
                      class="absolute -top-2 -right-2 z-50 rounded-full bg-white bg-opacity-50 p-1 text-white hover:bg-red-600 cursor-pointer"
                      @click.stop="removeFile(idx)"
                    >
                      <Close />
                    </el-icon>
                  </Motion>
                  <Motion
                    as="p"
                    :initial="{ opacity: 0 }"
                    :animate="{ opacity: 1 }"
                    class="flex-1 max-w-[8rem] break-words text-base text-neutral-700 dark:text-neutral-300"
                  >
                    {{ file.name }}
                  </Motion>
                  <Motion
                    as="p"
                    :initial="{ opacity: 0 }"
                    :animate="{ opacity: 1 }"
                    class="w-fit shrink-0 rounded-lg px-2 py-1 text-sm text-neutral-600 shadow-input dark:bg-neutral-800 dark:text-white"
                  >
                    {{ (file.size / (1024 * 1024)).toFixed(2) }} MB
                  </Motion>
                </div>

                <div
                  class="mt-2 flex w-full flex-col items-start justify-between text-sm text-neutral-600 md:flex-row md:items-center dark:text-neutral-400"
                >
                  <Motion
                    as="p"
                    :initial="{ opacity: 0 }"
                    :animate="{ opacity: 1 }"
                    class="rounded-md bg-gray-100 px-1.5 py-1 text-sm dark:bg-neutral-800"
                  >
                    {{ file.type || "unknown type" }}
                  </Motion>
                  <Motion
                    as="p"
                    :initial="{ opacity: 0 }"
                    :animate="{ opacity: 1 }"
                  >
                    modified {{ new Date(file.lastModified).toLocaleDateString() }}
                  </Motion>
                </div>
              </Motion>
            </template>
          </draggable>


            <template v-if="!files.length">
              <Motion
                as="div"
                class="relative z-40 mx-auto mt-4 flex h-32 w-full max-w-32 items-center justify-center rounded-md bg-white shadow-[0px_10px_50px_rgba(0,0,0,0.1)] group-hover/file:shadow-2xl dark:bg-neutral-900"
                :initial="{
                  x: 0,
                  y: 0,
                  opacity: 1,
                }"
                :transition="{
                  type: 'spring',
                  stiffness: 300,
                  damping: 20,
                }"
                :animate="
                  isActive
                    ? {
                        x: 20,
                        y: -20,
                        opacity: 0.9,
                      }
                    : {}
                "
              >
                <Icon
                  icon="heroicons:arrow-up-tray-20-solid"
                  class="text-neutral-600 dark:text-neutral-400"
                  size="20"
                />
              </Motion>

              <div
                class="absolute inset-0 z-30 mx-auto mt-4 flex h-32 w-full max-w-32 items-center justify-center rounded-md border border-dashed border-sky-400 bg-transparent transition-opacity"
                :class="{ 'opacity-100': isActive, 'opacity-0': !isActive }"
              ></div>
            </template>
          </div>
        </div>
      </div>
    </div>
  </ClientOnly>
</template>


<script lang="ts" setup>
import type { HTMLAttributes } from "vue";
import { cn } from "@/lib/utils";
import { Motion } from "motion-v";
import { ref } from "vue";
import { Icon } from '@iconify/vue';
import { onBeforeUnmount } from "vue";
import { ElImage } from 'element-plus';
import { defineEmits } from 'vue'
import draggable from "vuedraggable";
import { Close } from '@element-plus/icons-vue'
import { authAxios } from '@/utils/request'
import { useAuthStore } from '@/stores/auth' 

const { URL } = window;
const authStore = useAuthStore()

interface FileUploadProps {
  class?: HTMLAttributes["class"];
}

const removeFile = (index: number) => {
  // 释放预览对象URL，防止内存泄漏
  const file = files.value[index];
  if (file.previewUrl) URL.revokeObjectURL(file.previewUrl);

  files.value.splice(index, 1)
}


defineProps<FileUploadProps>();

const emit = defineEmits<{
  (e: "onChange", files: File[]): void;
}>();

const fileInputRef = ref<HTMLInputElement | null>(null);
const isActive = ref<boolean>(false);

type FileWithPreview = File & { previewUrl?: string; uploadData?: any; uploading?: boolean };

const files = ref<FileWithPreview[]>([]);

const uploadImage = async (file: File) => {
  const formData = new FormData();
  formData.append('image', file);

  try {
    // 假设你的token在localStorage，实际请根据你项目改
    const token = localStorage.getItem('token') || '';

    const res = await authAxios.post('/travel-posts/upload/img', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    if (res.data?.code === 200) {
      const data = res.data.data;
      if (data.url && data.imageId) {
        console.log('authStore keys:', Object.keys(authStore))
        authStore.setImageId(data.url, data.imageId)  // 保存映射
      }
      return data;
    } else {
      console.error('上传失败:', res.data?.message || '未知错误');
      return null;
    }
  } catch (err) {
    console.error('上传异常:', err);
    return null;
  }
};

function onSortUpdate() {
  emit("onChange", files.value); // 把排序后的顺序更新到父组件
}

async function handleFileChange(newFiles: File[]) {
  const enhancedFiles: FileWithPreview[] = newFiles.map((f) => {
    const fileWithPreview = f as FileWithPreview;
    if (f.type.startsWith("image/")) {
      fileWithPreview.previewUrl = URL.createObjectURL(f);
    }
    return fileWithPreview;
  });

  // 先把新文件加进 files 数组，标记上传中
  enhancedFiles.forEach(f => f.uploading = true);
  files.value = [...files.value, ...enhancedFiles];
  emit("onChange", files.value);

  // 依次上传新添加的文件
  for (const file of enhancedFiles) {
    const uploadResult = await uploadImage(file);
    file.uploading = false;
    if (uploadResult) {
      file.uploadData = uploadResult;
      console.log('上传成功：', uploadResult);
    } else {
      console.warn('上传失败的文件：', file.name);
    }
    emit("onChange", files.value);
  }
}

function onFileChange(e: Event) {
  const input = e.target as HTMLInputElement;
  if (!input.files) return;
  handleFileChange(Array.from(input.files));
}

function handleClick() {
  fileInputRef.value?.click();
}

function handleEnter() {
  isActive.value = true;
}
function handleLeave() {
  isActive.value = false;
}
function handleDrop(e: DragEvent) {
  isActive.value = false;
  const droppedFiles = e.dataTransfer?.files ? Array.from(e.dataTransfer.files) : [];
  if (droppedFiles.length) handleFileChange(droppedFiles);
}

// 销毁时释放预览链接
onBeforeUnmount(() => {
  files.value.forEach((file) => {
    if (file.previewUrl) {
      URL.revokeObjectURL(file.previewUrl);
    }
  });
});
</script>

<style scoped>
.group-hover\/file\:shadow-2xl:hover {
  box-shadow: 0px 10px 20px rgba(0, 0, 0, 0.25);
}

.transition-opacity {
  transition: opacity 0.3s ease;
}

/* 美化滚动条 */
::-webkit-scrollbar {
  width: 8px;
}
::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 4px;
}
::-webkit-scrollbar-thumb:hover {
  background: #555;
}

/* 拖动排序 */
.drag-ghost {
  opacity: 0.5;
}
.drag-handle {
  margin-right: 0.5rem;
  color: #888;
}

</style>