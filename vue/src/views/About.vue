<template>
  <div class="about">
    <h1>关于我们</h1>
    <div class="content">
      <h2>项目信息</h2>
      <p>这是一个标准的 Vue 3 项目，包含以下特性：</p>
      <ul>
        <li>Vue 3 Composition API</li>
        <li>Vue Router 4</li>
        <li>Vite 构建工具</li>
        <li>标准项目结构</li>
      </ul>
      <div class="info-card">
        <h3>技术栈</h3>
        <p>Vue 3 + Vue Router + Vite</p>
      </div>
    </div>
  </div>
</template>

<script setup>
// 使用 setup 语法，无需额外代码
</script>

<style scoped>
.about {
  padding: 40px 20px;
  max-width: 800px;
  margin: 0 auto;
}

h1 {
  color: #42b983;
  margin-bottom: 30px;
}

.content {
  text-align: left;
}

h2 {
  color: #2c3e50;
  margin-bottom: 20px;
}

ul {
  list-style-type: disc;
  padding-left: 30px;
  margin: 20px 0;
}

li {
  margin: 10px 0;
  color: #666;
  font-size: 16px;
}

.info-card {
  margin-top: 30px;
  padding: 20px;
  background-color: #e8f5e9;
  border-radius: 8px;
  border-left: 4px solid #42b983;
}

.info-card h3 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.info-card p {
  color: #666;
  font-size: 16px;
}
</style>

