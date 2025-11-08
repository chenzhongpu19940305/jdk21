<template>
  <div id="app">
    <nav class="navbar">
      <div class="nav-container">
        <h1 class="logo">Vue 3 应用</h1>
        <ul class="nav-menu">
          <li>
            <router-link to="/" class="nav-link">首页</router-link>
          </li>
          <li>
            <router-link to="/about" class="nav-link">关于</router-link>
          </li>
        </ul>
      </div>
    </nav>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
// 使用 setup 语法，无需额外代码
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
}
</style>

