<template>
  <el-aside width="200px">
    <el-scrollbar>
      <el-menu :default-openeds="[1, 2]">
        <el-menu-item index="1">
          <span>스케쥴</span>
        </el-menu-item>
        <el-menu-item index="2">
          <span>수강생</span>
        </el-menu-item>
        <el-menu-item index="3">
          <span>선생님</span>
        </el-menu-item>
        <el-menu-item index="4">
          <span>결제내역</span>
        </el-menu-item>
      </el-menu>
    </el-scrollbar>
  </el-aside>
</template>

<script setup lang="ts"></script>

<style scoped>
.el-aside {
  height: 100vh;
  color: var(--el-text-color-primary);
  background: var(--el-color-danger-light-8);
  border-right: 1px solid crimson;
}
.el-menu-item span {
  margin-left: 20px;
}
</style>
