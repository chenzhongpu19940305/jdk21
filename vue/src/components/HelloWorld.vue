<template>
  <div class="hello">
    <h2>{{ message }}</h2>
    <p>这是一个使用 Vue 3 Composition API 的示例组件</p>
    <button @click="increment">点击次数: {{ count }}</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  message: {
    type: String,
    default: 'Hello World'
  }
})

const count = ref(0)

const increment = () => {
  count.value++
}
</script>

<style scoped>
.hello {
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 8px;
  max-width: 600px;
  margin: 0 auto;
}

h2 {
  color: #42b983;
  margin-bottom: 16px;
}

p {
  color: #666;
  margin-bottom: 20px;
}

button {
  background-color: #42b983;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #35a372;
}
</style>












