<template>
  <el-container class="studentCreateContainer">
    <el-form
        ref="formRef"
        :model="student"
        label-width="auto"
        label-position="right"
        size="default"
    >
      <el-form-item
          prop="name"
          label="이름"
          :rules="nameRule"
      >
        <el-input v-model="student.name" />
      </el-form-item>
      <el-form-item
          prop="phoneNumber"
          label="연락처"
          :rules="phoneNumberRule"
      >
        <el-input v-model="student.phoneNumber"/>
      </el-form-item>
      <el-form-item
          prop="email"
          label="이메일"
          :rules="emailRule"
      >
        <el-input v-model="student.email" />
      </el-form-item>
      <el-form-item
          prop="course"
          label="수강과목"
          :rules="courseRule"
      >
        <el-select v-model="student.course">
          <el-option
              v-for="course in COURSE"
              :key="course.label"
              :label="course.label"
              :value="course.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item
          prop="grade"
          label="등급"
          :rules="gradeRule"
      >
        <el-select v-model="student.grade">
          <el-option
              v-for="grade in GRADE"
              :key="grade.label"
              :label="grade.label"
              :value="grade.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item
          prop="memo"
          label="특이사항"
      >
        <el-input v-model="student.memo" type="textarea" resize="false"/>
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
import {GRADE} from "/src/ts/grade";
import type {FormInstance} from "element-plus";
import axios from "axios";
import router from "@/router";
import {nameRule, phoneNumberRule, emailRule, courseRule, gradeRule } from "/src/validator/FormValidator"

const formRef = ref<FormInstance>()
const student = reactive<{
  name: string,
  email: string
  phoneNumber: string
  course: string
  grade: string
  memo: string
}>({
  name: '',
  email: '',
  phoneNumber: '',
  course: COURSE.PIANO.value,
  grade: GRADE.BEGINNER.value,
  memo: ''
})

const postApi = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate((valid) => {
    if (!valid) return false;
    axios.post("/api/students", student)
        .then(res => {
          alert("등록되었습니다.");
          router.replace({ name: "students"})}
        )
        .catch((err) => {
          const result = err.response.data;
          alert(result.message);
        });
  })
}


</script>

<style scoped>
.studentCreateContainer {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0;
}

.studentCreateContainer .el-form {
  width: 500px;
  margin-right: 20px;
}

.studentCreateContainer .el-select,
.studentCreateContainer .el-button{
  width: 100%;
}

</style>
