<template>
  <el-container class="studentsContainer">
    <el-input class="input-with-select">
      <template #append>
        <el-button :icon="Search" />
      </template>
    </el-input>
    <el-table class="studentTable" :data="tableData">
      <el-table-column prop="createAt" label="등록일" width="150" />
      <el-table-column prop="name" label="이름" width="150" />
      <el-table-column prop="phoneNumber" label="연락처" />
      <el-table-column prop="email" label="이메일" />
      <el-table-column prop="course" label="수강과목" width="150"/>
      <el-table-column prop="grade" label="등급" width="100"/>
    </el-table>
    <div class="pagination-block">
      <el-pagination layout="prev, pager, next" :total="tableData.length" :page-size="20" />
    </div>
  </el-container>

</template>

<script setup lang="ts">
import {ref} from "vue";
import { Search } from '@element-plus/icons-vue'

const tempDate = () => {
  const arr = [];
  // 페이지당 20개 정도가 적당
  for (let i = 0; i < 367; i++) {
    arr.push({
      createAt: '2016-05-07',
      name: 'Tom' + i,
      phoneNumber: '010-1111-2222',
      email: 'temp@naver.com',
      course: '바이올린',
      grade: '초급',
    });
  }
  return arr;
}
const tableData = ref(tempDate());
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


.studentTable {
  padding-top: 10px;
  max-width: 1000px;
  min-width: 900px;
}

.pagination-block {
  margin-top: 15px;
}

</style>
