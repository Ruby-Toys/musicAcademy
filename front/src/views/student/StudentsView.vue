<template>
  <el-container class="studentsContainer">
    <el-input class="input-with-select">
      <template #append>
        <el-button :icon="Search" />
      </template>
    </el-input>


    <el-table class="studentTable" :data="tableData">
      <el-table-column prop="date" label="등록일" width="180" />
      <el-table-column prop="name" label="이름" width="180" />
      <el-table-column prop="address" label="연락처" />
      <el-table-column prop="address" label="수강과목" />
      <el-table-column prop="address" label="담당 선생님" />
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
      date: '2016-05-07',
      name: 'Tom' + i,
      address: 'No. 189, Grove St, Los Angeles',
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
  width: 100%;
}

.pagination-block {
  margin-top: 15px;
}

</style>
