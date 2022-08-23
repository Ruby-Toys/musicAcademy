<template>
  <h1 class="main-text" @click="moveLogin()">Music Roost</h1>
</template>

<script setup lang="ts">
import router from "@/router";

const moveLogin = () => {
  router.push({ name: "login" });
};
</script>

<style scoped>
.main-text {
  box-sizing: content-box;
  font-size: 100px;
  cursor: pointer;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translateX(-50%) translateY(-50%);
  margin: 0;
}
</style>
