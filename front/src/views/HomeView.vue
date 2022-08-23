<template>
  <h1 class="main-text" @click="moveLogin()">Music Roost</h1>
</template>

<script setup lang="ts">
import router from "@/router";

const moveLogin = () => {
  router.push({name: "login"});
}
</script>

<style scoped>
.main-text {
  font-size: 100px;
  cursor: pointer;
}
</style>
