<template>
  <el-container class="teacherCreateContainer">
    <el-form
        ref="formRef"
        :model="teacher"
        label-width="auto"
        label-position="right"
        size="default"
    >
      <el-form-item
          prop="name"
          label="이름"
          :rules="nameRule"
      >
        <el-input v-model="teacher.name" />
      </el-form-item>
      <el-form-item
          prop="phoneNumber"
          label="연락처"
          :rules="phoneNumberRule"
      >
        <el-input v-model="teacher.phoneNumber"/>
      </el-form-item>
      <el-form-item
          prop="email"
          label="이메일"
          :rules="emailRule"
      >
        <el-input v-model="teacher.email" />
      </el-form-item>
      <el-form-item
          prop="course"
          label="담당과목"
          :rules="courseRule"
      >
        <el-select v-model="teacher.course">
          <el-option
              v-for="course in COURSE"
              :key="course.label"
              :label="course.label"
              :value="course.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="postApi(formRef)">등록</el-button>
      </el-form-item>
    </el-form>
  </el-container>
</template>

<script setup lang="ts">
import {reactive, ref} from "vue";
import {COURSE} from "/src/ts/course";
import type {FormInstance} from "element-plus";
import axios from "axios";
import router from "@/router";
import {nameRule, phoneNumberRule, emailRule, courseRule } from "/src/validator/FormValidator"

const formRef = ref<FormInstance>()
const teacher = reactive<{
  name: string,
  email: string
  phoneNumber: string
  course: string
}>({
  name: '',
  email: '',
  phoneNumber: '',
  course: COURSE.PIANO.value,
})

const postApi = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate((valid) => {
    if (!valid) return false;
    axios.post("/api/teachers", teacher)
        .then(res => {
          alert("등록되었습니다.");
          router.replace({ name: "teachers"})}
        )
        .catch((err) => {
          const result = err.response.data;
          alert(result.message);
        });
  })
}


</script>

<style scoped>
.teacherCreateContainer {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0;
}

.teacherCreateContainer .el-form {
  width: 500px;
  margin-right: 20px;
}

.teacherCreateContainer .el-select,
.teacherCreateContainer .el-button{
  width: 100%;
}

</style>
