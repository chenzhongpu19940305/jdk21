<template>
  <div class="home">
    <h1>首页</h1>
    <p>欢迎来到 Vue 3 应用首页</p>
    <div class="content">
      <p>这是一个使用 Vue Router 的标准 Vue 3 项目</p>
      <p>当前时间: {{ currentTime }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const currentTime = ref(new Date().toLocaleString('zh-CN'))

let timer = null

onMounted(() => {
  timer = setInterval(() => {
    currentTime.value = new Date().toLocaleString('zh-CN')
  }, 1000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
.home {
  padding: 40px 20px;
  max-width: 800px;
  margin: 0 auto;
}

h1 {
  color: #42b983;
  margin-bottom: 20px;
}

.content {
  margin-top: 30px;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.content p {
  margin: 10px 0;
  color: #666;
  font-size: 16px;
}
</style>

