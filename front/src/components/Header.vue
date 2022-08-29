<template>
  <el-menu mode="horizontal" :ellipsis="false">
    <span class="header-title" @click="moveHome">Music Academy</span>
    <el-menu-item index="1" @click="moveSchedules">스케줄</el-menu-item>
    <el-sub-menu index="2">
      <template #title>수강생</template>
      <el-menu-item index="2-1" @click="moveStudents">목록 조회</el-menu-item>
      <el-menu-item index="2-2" @click="moveStudentPost">등록</el-menu-item>
    </el-sub-menu>
    <el-sub-menu index="3">
      <template #title>선생님</template>
      <el-menu-item index="3-1" @click="moveTeachers">목록 조회</el-menu-item>
      <el-menu-item index="3-2" @click="moveTeacherPost">등록</el-menu-item>
    </el-sub-menu>
    <el-menu-item index="4" @click="movePayments">결제 내역</el-menu-item>
    <div class="flex-grow" />
    <span @click="docs">API Docs</span>
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
const moveStudentPost = () => {
  router.push({ name: "studentPost" });
};
const moveTeachers = () => {
  router.push({ name: "teachers" });
};
const moveTeacherPost = () => {
  router.push({ name: "teacherPost" });
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
const docs = () => {
  location.href = "http://localhost:8080/docs/index.html"
}
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
