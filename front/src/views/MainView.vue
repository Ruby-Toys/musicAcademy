<template>
  <el-container>
    <el-header>
      <Header />
    </el-header>
    <el-main class="mainContainer">
      <RouterView />
    </el-main>
  </el-container>
</template>

<script lang="ts" setup>
import Header from "@/components/Header.vue";
</script>

<style scoped>
.el-header,
.el-main {
  width: 100%;
  padding: 0;
}
.el-header {
  box-sizing: border-box;
  height: 60px;
}
.mainContainer {
  height: calc(100vh - 60px);
  padding: 0 200px;
}
</style>
