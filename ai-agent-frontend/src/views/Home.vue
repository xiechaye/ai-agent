<template>
  <div class="home-container">
    <h1 class="title">AI 智能体应用平台</h1>
    <div class="card-container">
      <router-link to="/love-app" class="card">
        <el-card shadow="hover">
          <h2>AI 恋爱大师</h2>
          <p>智能恋爱咨询，让你的情感生活更美好</p>
          <el-button type="primary">进入应用</el-button>
        </el-card>
      </router-link>
      
      <router-link to="/love-manus" class="card">
        <el-card shadow="hover">
          <h2>AI 超级智能体</h2>
          <p>强大的智能助手，解答你的各种问题</p>
          <el-button type="primary">进入应用</el-button>
        </el-card>
      </router-link>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  text-align: center;
}

.title {
  font-size: 36px;
  color: #333;
  margin-bottom: 40px;
}

.card-container {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 30px;
}

.card {
  width: 300px;
  text-decoration: none;
  color: inherit;
  transition: transform 0.3s;
}

.card:hover {
  transform: translateY(-5px);
}

.el-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.el-card h2 {
  font-size: 24px;
  margin-bottom: 16px;
  color: #409EFF;
}

.el-card p {
  flex: 1;
  margin-bottom: 20px;
  color: #666;
}
</style> 