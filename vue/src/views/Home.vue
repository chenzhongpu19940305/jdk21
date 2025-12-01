<template>
  <div class="home">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-content">
        <h1 class="hero-title">Vue 3 应用</h1>
        <p class="hero-subtitle">现代化的前端体验</p>
        <div class="time-display">{{ currentTime }}</div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features">
      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">🚀</div>
          <h3>高性能</h3>
          <p>使用 Vue 3 Composition API 构建高效应用</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">🎨</div>
          <h3>现代设计</h3>
          <p>响应式布局，完美适配各种屏幕尺寸</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">⚡</div>
          <h3>快速开发</h3>
          <p>使用 Vite 构建工具，开发体验极佳</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">🔧</div>
          <h3>易于维护</h3>
          <p>模块化架构，代码结构清晰易维护</p>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta">
      <h2>开始探索</h2>
      <p>点击导航菜单了解更多功能</p>
      <div class="cta-buttons">
        <router-link to="/bilibili" class="btn btn-primary">哔哩哔哩</router-link>
        <router-link to="/juejin" class="btn btn-primary">掘金风格</router-link>
        <router-link to="/web-content" class="btn btn-secondary">网页阅读器</router-link>
      </div>
    </section>
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
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow-y: auto;
  overflow-x: hidden;
}

/* Hero Section */
.hero {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  color: white;
}

.hero-content {
  max-width: 600px;
  animation: fadeInUp 0.8s ease-out;
}

.hero-title {
  font-size: 4rem;
  font-weight: 700;
  margin-bottom: 20px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.hero-subtitle {
  font-size: 1.5rem;
  margin-bottom: 40px;
  opacity: 0.95;
  font-weight: 300;
}

.time-display {
  font-size: 1.2rem;
  padding: 15px 30px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: inline-block;
  font-weight: 500;
}

/* Features Section */
.features {
  padding: 80px 20px;
  background: white;
}

.features-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.feature-card {
  padding: 40px 30px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 12px;
  text-align: center;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.feature-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 15px;
}

.feature-card h3 {
  font-size: 1.3rem;
  color: #2c3e50;
  margin-bottom: 10px;
}

.feature-card p {
  color: #666;
  font-size: 0.95rem;
  line-height: 1.6;
}

/* CTA Section */
.cta {
  padding: 80px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
}

.cta h2 {
  font-size: 2.5rem;
  margin-bottom: 15px;
  font-weight: 700;
}

.cta p {
  font-size: 1.1rem;
  margin-bottom: 40px;
  opacity: 0.95;
}

.cta-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn {
  padding: 15px 40px;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 50px;
  text-decoration: none;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  cursor: pointer;
  display: inline-block;
}

.btn-primary {
  background: white;
  color: #667eea;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.btn-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
}

.btn-secondary {
  background: transparent;
  color: white;
  border: 2px solid white;
}

.btn-secondary:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-3px);
}

/* Animations */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Responsive Design */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }

  .hero-subtitle {
    font-size: 1.2rem;
  }

  .cta h2 {
    font-size: 2rem;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .cta-buttons {
    flex-direction: column;
  }

  .btn {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .hero {
    padding: 40px 15px;
  }

  .hero-title {
    font-size: 2rem;
  }

  .hero-subtitle {
    font-size: 1rem;
  }

  .features {
    padding: 40px 15px;
  }

  .cta {
    padding: 40px 15px;
  }
}
</style>









