<template>
  <el-container class="d-flex justify-content-center align-items-center w-100 h-100">
    <h1 class="w-50 text-center main-text"  @click="moveLogin()">Music Roost</h1>
  </el-container>
</template>

<script setup lang="ts">
import router from "@/router";

const moveLogin = () => {
  router.push({name: "login"});
}
</script>

<style scoped>
  .main-text {
    font-size: 60px;
  }
</style>
