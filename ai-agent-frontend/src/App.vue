<script setup lang="ts">
import { onMounted } from 'vue';
import FooterBar from './components/FooterBar.vue';

// 在移动设备上调整视口高度，解决100vh问题
onMounted(() => {
  const setAppHeight = () => {
    document.documentElement.style.setProperty('--app-height', `${window.innerHeight}px`);
  };
  
  window.addEventListener('resize', setAppHeight);
  setAppHeight();
  
  return () => window.removeEventListener('resize', setAppHeight);
});
</script>

<template>
  <div class="app-container">
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    <FooterBar />
  </div>
</template>

<style>
:root {
  --app-height: 100vh;
}

.app-container {
  display: flex;
  flex-direction: column;
  min-height: var(--app-height);
  width: 100%;
  position: relative;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
