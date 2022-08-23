<template>
  <el-container class="teachersContainer">
    <h1>Teacher</h1>
    <el-table class="teachersTable" :data="teachers" @row-click="moveInfo">
      <el-table-column prop="createAt" label="등록일" width="150" />
      <el-table-column prop="name" label="이름" width="150" />
      <el-table-column prop="phoneNumber" label="연락처" />
      <el-table-column prop="email" label="이메일" />
      <el-table-column prop="course" label="수강과목" width="150"/>
    </el-table>
  </el-container>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import {COURSE} from "/src/js/course";
import axios from "axios";
import router from "@/router";

const teachers = ref([]);
const getList = () => {
  axios.get("/api/teachers")
      .then(res => {
        const teachersPage = res.data;
        teachers.value = [];
        teachersPage.contents.forEach(teacher => {
          teacher.course = COURSE[teacher.course].label;
          teachers.value.push(teacher);
        })
      })
      .catch(err => {
        const result = err.response.data;
        alert(result.message);
      });
}

const moveInfo = (row) => {
  router.push({ name: "teacherInfo" , params: {studentId: row.id}});
}

onMounted(() => {
  getList();
})
</script>

<style scoped>
.teachersContainer {
  padding: 50px 0 100px;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.teachersTable {
  padding-top: 10px;
  max-width: 1000px;
  min-width: 900px;
  cursor: pointer;
}
</style>
