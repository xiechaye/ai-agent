<template>
  <div class="home-container">
    <div class="container">
      <h1 class="title fade-in">AI 智能体应用平台</h1>
      <p class="subtitle fade-in">选择您需要的智能服务</p>
      
      <div class="card-container fade-in">
        <router-link to="/love-app" class="app-card">
          <div class="card">
            <div class="card-header">
              <div class="card-icon">
                <el-icon><ChatLineRound /></el-icon>
              </div>
              <h2>AI 恋爱大师</h2>
            </div>
            <div class="card-body">
              <p>智能恋爱咨询，让你的情感生活更美好</p>
              <div class="card-features">
                <span class="feature-tag">情感分析</span>
                <span class="feature-tag">关系建议</span>
                <span class="feature-tag">多模式对话</span>
              </div>
            </div>
            <div class="card-footer">
              <el-button type="primary" round>进入应用</el-button>
            </div>
          </div>
        </router-link>
        
        <router-link to="/love-manus" class="app-card">
          <div class="card">
            <div class="card-header">
              <div class="card-icon">
                <el-icon><Connection /></el-icon>
              </div>
              <h2>AI 超级智能体</h2>
            </div>
            <div class="card-body">
              <p>强大的智能助手，解答你的各种问题</p>
              <div class="card-features">
                <span class="feature-tag">知识检索</span>
                <span class="feature-tag">工具调用</span>
                <span class="feature-tag">实时分析</span>
              </div>
            </div>
            <div class="card-footer">
              <el-button type="primary" round>进入应用</el-button>
            </div>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ChatLineRound, Connection } from '@element-plus/icons-vue'

const router = useRouter()
</script>

<style scoped>
.home-container {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-lg);
  background: linear-gradient(135deg, var(--bg-primary) 0%, var(--bg-secondary) 100%);
}

.title {
  font-size: 2.5rem;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
  text-align: center;
  font-weight: 600;
}

.subtitle {
  font-size: var(--font-size-lg);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-xl);
  text-align: center;
}

.card-container {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: var(--spacing-xl);
  margin-top: var(--spacing-lg);
  width: 100%;
}

.app-card {
  flex: 1;
  min-width: 280px;
  max-width: 400px;
  text-decoration: none;
  color: inherit;
}

.card {
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: var(--border-radius-large);
  box-shadow: var(--box-shadow);
  transition: transform var(--transition-normal), box-shadow var(--transition-normal);
  background-color: var(--bg-secondary);
  overflow: hidden;
}

.card:hover {
  transform: translateY(-8px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.12);
}

.card-header {
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  align-items: center;
  border-bottom: 1px solid var(--border-light);
}

.card-icon {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--primary-light);
  color: var(--primary-color);
  border-radius: 50%;
  margin-bottom: var(--spacing-md);
}

.card-icon svg {
  width: 30px;
  height: 30px;
}

.card-header h2 {
  font-size: var(--font-size-xl);
  margin-bottom: 0;
  color: var(--primary-color);
}

.card-body {
  padding: var(--spacing-lg);
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-body p {
  color: var(--text-regular);
  margin-bottom: var(--spacing-lg);
  text-align: center;
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
  color: var(--primary-color);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: var(--font-size-xs);
  font-weight: 500;
}

.card-footer {
  padding: var(--spacing-lg);
  display: flex;
  justify-content: center;
  background-color: var(--bg-tertiary);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .home-container {
    padding: var(--spacing-md);
  }
  
  .title {
    font-size: 1.8rem;
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
}

@media (max-width: 480px) {
  .title {
    font-size: 1.5rem;
  }
  
  .card-header {
    padding: var(--spacing-md);
  }
  
  .card-body, .card-footer {
    padding: var(--spacing-md);
  }
}
</style> 