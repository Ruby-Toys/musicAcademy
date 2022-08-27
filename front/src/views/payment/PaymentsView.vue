<template>
  <el-container class="paymentsContainer">
    <h1>Payment</h1>
    <div>
      <el-input class="input-with-select" v-model="word">
        <template #append>
          <el-button :icon="Search" @click="search"/>
        </template>
      </el-input>
    </div>
    <el-table class="paymentsTable" :data="payments">
      <el-table-column prop="paymentDate" label="결제일"/>
      <el-table-column prop="studentName" label="이름" />
      <el-table-column prop="phoneNumber" label="연락처" />
      <el-table-column prop="amount" label="결제금액" :formatter="paymentFormatter"/>
      <el-table-column fixed="right" width="120">
        <template #default="scope">
          <el-button
              link
              type="primary"
              size="small"
              @click.prevent="deleteApi(scope.row.id)"
          >
            결제취소
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-block">
      <el-pagination layout="prev, pager, next" :total="totalCount" :page-size="pageSize" @current-change="getListApi"/>
    </div>
  </el-container>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import { Search } from '@element-plus/icons-vue'
import axios from "axios";

const payments = ref([]);
const word = ref('');
const searchForm = ref({
  word: "",
  page: 1
})
const pageSize = ref(15);
const totalCount = ref(0);
const search = () => {
  searchForm.value.word = word.value;
  getListApi(1);
}

const getListApi = (page) => {
  searchForm.value.page = page;
  axios.get("/api/payments", {params: searchForm.value})
      .then(res => {
        const paymentsPage = res.data;
        pageSize.value = paymentsPage.pageSize;
        totalCount.value = paymentsPage.totalCount;
        payments.value = [];
        paymentsPage.contents.forEach(payment => {
          payments.value.push(payment);
        })
      })
      .catch(err => {
        const result = err.response.data;
        alert(result.message);
      });
}
const deleteApi = (paymentId) => {
  if (!confirm("해당 결제를 취소하시겠습니까?")) return;

  axios.delete(`/api/payments/${paymentId}`)
      .then(res => {
        alert("결제가 취소되었습니다.")
        getListApi(searchForm.value.page);
      })
      .catch(err => {
        const result = err.response.data;
        alert(result.message);
      });
}


const paymentFormatter = (payment) => {
  return payment.amount.toLocaleString('en');
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

.paymentsContainer {
  padding: 50px 0 100px;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.paymentsTable {
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
