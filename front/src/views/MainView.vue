<template>
  <el-container>
    <el-header>
      <Header/>
    </el-header>
    <el-main>
      <RouterView />
    </el-main>
  </el-container>
</template>

<script lang="ts" setup>
import Header from "@/components/Header.vue";
import Aside from "@/components/Aside.vue";
</script>

<style scoped>
.el-header, .el-main {
  width: 100%;
  padding: 0;
}
.el-header {
  box-sizing: border-box;
  height: 60px;
}
.el-main {
  height: calc(100vh - 60px);
  padding: 0 200px;
  display: flex;
  justify-content: center;
  align-items: center;
}

</style>