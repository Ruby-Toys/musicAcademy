<template>
  <el-menu mode="horizontal" :ellipsis="false">
    <span class="header-title" @click="moveHome">Music Academy</span>
    <el-menu-item index="1" @click="moveSchedules">스케줄</el-menu-item>
    <el-menu-item index="2" @click="moveStudents">수강생</el-menu-item>
    <el-menu-item index="3" @click="moveTeachers">선생님</el-menu-item>
    <el-menu-item index="4" @click="movePayments">결제 내역</el-menu-item>
    <div class="flex-grow" />
    <span>계정 관리</span>
    <span @click="logout">로그아웃</span>
  </el-menu>
</template>

<script setup lang="ts">
import router from "@/router";
import axios from "axios";

const moveHome = () => {
  router.push({ name: "home" });
};
const moveSchedules = () => {
  router.push({ name: "schedules" });
};
const moveStudents = () => {
  router.push({ name: "students" });
};
const moveTeachers = () => {
  router.push({ name: "teachers" });
};
const movePayments = () => {
  router.push({ name: "payments" });
};
const logout = () => {
  axios
      .get("/api/logout")
      .then(() => {
        router.replace({ name: "home" });
      });
};
</script>

<style scoped>
.el-menu {
  width: 100%;
  padding: 0 200px;
}

.el-menu span {
  cursor: pointer;
  height: 60px;
  line-height: 60px;
}

.el-menu span:not(:first-child) {
  margin-left: 15px;
}

.el-menu--horizontal > .el-menu-item.is-active,
.el-menu--horizontal > .el-menu-item {
  border-bottom: none;
  height: 60px;
}

.header-title {
  font-size: 30px;
  font-weight: 600;
  margin-right: 30px;
}

.flex-grow {
  flex-grow: 1;
}
</style>
