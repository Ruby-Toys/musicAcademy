<template>
  <el-form
    label-position="top"
    label-width="100px"
    :model="loginForm"
    style="max-width: 460px"
  >
    <el-form-item label="Name">
      <el-input v-model="loginForm.name" />
    </el-form-item>
    <el-form-item label="Password">
      <el-input v-model="loginForm.password" type="password"/>
    </el-form-item>
    <el-button type="primary" @click="loginApi">로그인</el-button>
  </el-form>
</template>

<script lang="ts" setup>
import {reactive} from "vue";
import axios from "axios";
import router from "@/router";

const loginForm = reactive({
  name: "",
  password: "",
});

const loginApi = () => {
  axios
    .post("/api/login", loginForm)
    .then(() => {
      // 로그인에 성공하면 켈린더 스케쥴 페이지로 넘어감!
      router.replace({ name: "schedules" });
    })
    .catch((err) => {
      const result = err.response.data;
      alert(result.message);
    });
};

</script>

<style scoped>
.el-form {
  width: 400px;
  height: 300px;
  position: relative;
  top: 50%;
  left: 50%;
  transform: translateX(-50%) translateY(-50%);
}

.el-button {
  margin-top: 10px;
  width: 100%;
}
</style>
