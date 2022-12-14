<template>
  <el-container class="studentsContainer">
    <h1>Student</h1>
    <div>
      <el-input class="input-with-select" v-model="word">
        <template #append>
          <el-button :icon="Search" @click="search"/>
        </template>
      </el-input>
    </div>
    <el-table class="studentsTable" :data="students" @row-click="moveInfo">
      <el-table-column prop="createAt" label="등록일" width="150" />
      <el-table-column prop="name" label="이름" width="150" />
      <el-table-column prop="phoneNumber" label="연락처" />
      <el-table-column prop="email" label="이메일" />
      <el-table-column prop="course" label="수강과목" width="150" :formatter="courseFormatter" />
      <el-table-column prop="grade" label="등급" width="100" :formatter="gradeFormatter" />
    </el-table>
    <div class="pagination-block">
      <el-pagination layout="prev, pager, next" :total="totalCount" :page-size="pageSize" @current-change="getListApi"/>
    </div>
  </el-container>

</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import { Search } from '@element-plus/icons-vue'
import {COURSE} from "/src/ts/course";
import {GRADE} from "/src/ts/grade";
import axios from "axios";
import router from "@/router";
import {useStudentStore} from "@/store/studentStore";

const students = ref([]);
const word = ref('');
const searchForm = ref({
  word: "",
  page: 1
})
const pageSize = ref(15);
const totalCount = ref(0);
const getListApi = (page) => {
  searchForm.value.page = page;
  axios.get("/api/students", {params: searchForm.value})
      .then(res => {
        const studentsPage = res.data;
        pageSize.value = studentsPage.pageSize;
        totalCount.value = studentsPage.totalCount;
        students.value = [];
        studentsPage.contents.forEach(student => {
          students.value.push(student);
        })
      })
      .catch(err => {
        const result = err.response.data;
        alert(result.message);
      });
}
const search = () => {
  searchForm.value.word = word.value;
  getListApi(1);
}

const courseFormatter = (student) => {
  return COURSE[student.course].label;
}
const gradeFormatter = (student) => {
  return GRADE[student.grade].label;
}

const moveInfo = (student) => {
  const studentStore = useStudentStore();
  studentStore.set(student);
  router.push({ name: "studentInfo"});
}

onMounted(() => {
  getListApi(1);
})
</script>

<style scoped>

.input-with-select {
  width: 30%;
  min-width: 300px;
}

.studentsContainer {
  padding: 50px 0 100px;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.studentsTable {
  padding-top: 10px;
  max-width: 1000px;
  min-width: 900px;
  cursor: pointer;
}

.el-table-column {
  display: flex;
  justify-content: center;
}

.pagination-block {
  margin-top: 15px;
}

</style>
