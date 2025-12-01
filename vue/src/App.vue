<template>
  <div id="app">
    <nav v-if="!isFullScreenPage" class="navbar">
      <div class="nav-container">
        <h1 class="logo">Vue 3 应用</h1>
        <ul class="nav-menu">
          <li>
            <router-link to="/" class="nav-link">首页</router-link>
          </li>
          <li>
            <router-link to="/bilibili" class="nav-link">哔哩哔哩</router-link>
          </li>
          <li>
            <router-link to="/juejin" class="nav-link">掘金风格</router-link>
          </li>
          <li>
            <router-link to="/web-content" class="nav-link">网页阅读器</router-link>
          </li>
        </ul>
      </div>
    </nav>
    <main :class="['main-content', { 'full-screen': isFullScreenPage }]">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const isJuejinPage = computed(() => {
  return route.name === 'Juejin'
})

const isFullScreenPage = computed(() => {
  return route.name === 'Home' || route.name === 'Bilibili' || route.name === 'Juejin' || route.name === 'WebContentReader' || route.name === 'ArticleDetail'
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  background-color: #2c3e50;
  padding: 1rem 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  color: #42b983;
  font-size: 1.5rem;
  font-weight: bold;
}

.nav-menu {
  display: flex;
  list-style: none;
  gap: 2rem;
}

.nav-link {
  color: #fff;
  text-decoration: none;
  font-size: 1rem;
  transition: color 0.3s;
  padding: 0.5rem 1rem;
  border-radius: 4px;
}

.nav-link:hover {
  color: #42b983;
  background-color: rgba(66, 185, 131, 0.1);
}

.nav-link.router-link-active {
  color: #42b983;
  background-color: rgba(66, 185, 131, 0.2);
}

.main-content {
  flex: 1;
  padding: 40px 20px;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.main-content.full-screen {
  padding: 0;
  max-width: 100%;
  margin: 0;
  height: 100vh;
}
</style>

