<template>
  <main class="home-container">
    <div class="decor-circle decor-circle-1"></div>
    <div class="decor-circle decor-circle-2"></div>
    <div class="decor-circle decor-circle-3"></div>
    
    <div class="container">
      <div class="welcome-section">
        <h1 class="title fade-in">AI 智能体应用平台</h1>
        <p class="subtitle fade-in">为您提供智能、温暖、贴心的服务</p>
        <div class="divider">
          <span class="divider-line"></span>
          <span class="divider-icon">✦</span>
          <span class="divider-line"></span>
        </div>
      </div>
      
      <section class="card-container fade-in">
        <router-link to="/love-app" class="app-card" aria-label="进入AI恋爱大师应用">
          <article class="card">
            <div class="card-ribbon"></div>
            <header class="card-header">
              <div class="card-icon">
                <el-icon><ChatLineRound /></el-icon>
              </div>
              <h2>AI 恋爱大师</h2>
            </header>
            <div class="card-body">
              <p>智能恋爱咨询，让你的情感生活更美好</p>
              <div class="card-features">
                <span class="feature-tag">情感分析</span>
                <span class="feature-tag">关系建议</span>
                <span class="feature-tag">多模式对话</span>
              </div>
            </div>
            <footer class="card-footer">
              <el-button type="primary" round>开始对话</el-button>
            </footer>
          </article>
        </router-link>
        
        <router-link to="/love-manus" class="app-card" aria-label="进入AI超级智能体应用">
          <article class="card">
            <div class="card-ribbon"></div>
            <header class="card-header">
              <div class="card-icon">
                <el-icon><Connection /></el-icon>
              </div>
              <h2>AI 超级智能体</h2>
            </header>
            <div class="card-body">
              <p>强大的智能助手，解答你的各种问题</p>
              <div class="card-features">
                <span class="feature-tag">知识检索</span>
                <span class="feature-tag">工具调用</span>
                <span class="feature-tag">实时分析</span>
              </div>
            </div>
            <footer class="card-footer">
              <el-button type="primary" round>开始对话</el-button>
            </footer>
          </article>
        </router-link>
      </section>
      
      <div class="trust-indicators">
        <div class="trust-item">
          <div class="trust-icon">✓</div>
          <div class="trust-text">隐私保护</div>
        </div>
        <div class="trust-item">
          <div class="trust-icon">✓</div>
          <div class="trust-text">安全可靠</div>
        </div>
        <div class="trust-item">
          <div class="trust-icon">✓</div>
          <div class="trust-text">专业服务</div>
        </div>
      </div>
    </div>
  </main>
</template>

<script lang="ts" setup>
import { ChatLineRound, Connection } from '@element-plus/icons-vue'
</script>

<style scoped>
.home-container {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl) var(--spacing-lg);
  background: linear-gradient(135deg, var(--soft-lavender) 0%, var(--bg-primary) 100%);
  position: relative;
  overflow: hidden;
}

.decor-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.5;
  z-index: 0;
}

.decor-circle-1 {
  width: 300px;
  height: 300px;
  background-color: var(--soft-pink);
  top: -100px;
  right: -100px;
  filter: blur(80px);
}

.decor-circle-2 {
  width: 200px;
  height: 200px;
  background-color: var(--soft-mint);
  bottom: -50px;
  left: -50px;
  filter: blur(60px);
}

.decor-circle-3 {
  width: 150px;
  height: 150px;
  background-color: var(--soft-yellow);
  top: 30%;
  left: 10%;
  filter: blur(70px);
}

.welcome-section {
  text-align: center;
  margin-bottom: var(--spacing-xxl);
  position: relative;
  z-index: 1;
}

.title {
  font-size: 2.5rem;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
  text-align: center;
  font-weight: 600;
  letter-spacing: -0.5px;
}

.subtitle {
  font-size: var(--font-size-lg);
  color: var(--text-regular);
  margin-bottom: var(--spacing-lg);
  text-align: center;
  font-weight: 400;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.divider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: var(--spacing-lg) 0;
}

.divider-line {
  height: 1px;
  width: 80px;
  background: linear-gradient(to right, transparent, var(--primary-color), transparent);
}

.divider-icon {
  margin: 0 var(--spacing-md);
  color: var(--primary-color);
  font-size: 14px;
}

.card-container {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: var(--spacing-xl);
  width: 100%;
  position: relative;
  z-index: 1;
  padding: var(--spacing-md);
}

.app-card {
  flex: 1;
  min-width: 280px;
  max-width: 400px;
  text-decoration: none;
  color: inherit;
  transition: transform var(--transition-normal);
}

.app-card:hover {
  transform: translateY(-5px);
}

.card {
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: var(--border-radius-large);
  box-shadow: var(--box-shadow);
  transition: box-shadow var(--transition-normal), transform var(--transition-normal);
  background-color: var(--bg-secondary);
  overflow: hidden;
  position: relative;
  border: 1px solid var(--border-light);
}

.card:hover {
  box-shadow: var(--box-shadow-hover);
}

.card-ribbon {
  position: absolute;
  width: 100px;
  height: 100px;
  top: -50px;
  right: -50px;
  overflow: hidden;
  transform: rotate(45deg);
  z-index: 1;
}

.card-ribbon:before {
  content: '';
  position: absolute;
  width: 150%;
  height: 25px;
  background-color: var(--soft-pink);
  top: 70px;
  left: -25px;
  opacity: 0.7;
}

.app-card:nth-child(2) .card-ribbon:before {
  background-color: var(--soft-mint);
}

.card-header {
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  align-items: center;
  border-bottom: 1px solid var(--border-light);
  background-color: var(--bg-tertiary);
  position: relative;
}

.card-icon {
  width: 70px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--primary-light);
  color: var(--primary-color);
  border-radius: 50%;
  margin-bottom: var(--spacing-md);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: transform var(--transition-normal);
}

.app-card:hover .card-icon {
  transform: scale(1.05);
}

.card-icon svg {
  width: 32px;
  height: 32px;
  transition: transform var(--transition-normal);
}

.app-card:hover .card-icon svg {
  transform: scale(1.1);
}

.card-header h2 {
  font-size: var(--font-size-xl);
  margin-bottom: var(--spacing-sm);
  color: var(--primary-color);
  font-weight: 500;
}

.card-body {
  padding: var(--spacing-lg);
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-secondary);
}

.card-body p {
  color: var(--text-regular);
  margin-bottom: var(--spacing-lg);
  text-align: center;
  line-height: 1.6;
}

.card-features {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  justify-content: center;
  margin-top: auto;
}

.feature-tag {
  background-color: var(--primary-light);
  color: var(--primary-dark);
  padding: 6px 14px;
  border-radius: var(--border-radius-large);
  font-size: var(--font-size-xs);
  font-weight: 500;
  transition: all var(--transition-normal);
}

.app-card:hover .feature-tag {
  background-color: var(--primary-color);
  color: white;
}

.card-footer {
  padding: var(--spacing-lg);
  display: flex;
  justify-content: center;
  background-color: var(--bg-secondary);
  border-top: 1px solid var(--border-light);
}

.trust-indicators {
  display: flex;
  justify-content: center;
  gap: var(--spacing-xl);
  margin-top: var(--spacing-xxl);
  margin-bottom: var(--spacing-lg);
  position: relative;
  z-index: 1;
}

.trust-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.trust-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

.trust-text {
  color: var(--text-regular);
  font-size: var(--font-size-sm);
  font-weight: 500;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .home-container {
    padding: var(--spacing-lg) var(--spacing-md);
  }
  
  .title {
    font-size: 2rem;
  }
  
  .subtitle {
    font-size: var(--font-size-md);
  }
  
  .card-container {
    flex-direction: column;
    align-items: center;
    gap: var(--spacing-lg);
  }
  
  .app-card {
    width: 100%;
  }
  
  .trust-indicators {
    flex-direction: column;
    align-items: center;
    gap: var(--spacing-md);
  }
}

@media (max-width: 480px) {
  .title {
    font-size: 1.8rem;
  }
  
  .card-header {
    padding: var(--spacing-md);
  }
  
  .card-body, .card-footer {
    padding: var(--spacing-md);
  }
  
  .divider-line {
    width: 50px;
  }
}
</style> 