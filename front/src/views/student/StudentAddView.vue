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
          :rules="[
            {
              required: true,
              message: '이름을 입력해주세요.',
              trigger: 'blur',
            },
            {
              type: 'string',
              message: '이름 형식이 올바르지 않습니다.',
              trigger: ['blur', 'change'],
              pattern: /^[가-힣a-zA-Z\d]{2,20}$/
            },
          ]"
      >
        <el-input v-model="student.name" />
      </el-form-item>
      <el-form-item
          prop="phoneNumber"
          label="연락처"
          :rules="[
            {
              required: true,
              message: '연락처를 입력해주세요.',
              trigger: 'blur',
            },
            {
              type: 'string',
              message: '연락처 형식이 올바르지 않습니다.',
              trigger: ['blur', 'change'],
              pattern: /^(010|011|016|017|019)\d{3,4}\d{4}$/
            },
          ]"
      >
        <el-input v-model="student.phoneNumber"/>
      </el-form-item>
      <el-form-item
          prop="email"
          label="이메일"
          :rules="[
            {
              required: true,
              message: '이메일을 입력해주세요.',
              trigger: 'blur',
            },
            {
              type: 'email',
              message: '이메일 형식이 올바르지 않습니다.',
              trigger: ['blur', 'change'],
            },
          ]"
      >
        <el-input v-model="student.email" />
      </el-form-item>
      <el-form-item
          prop="course"
          label="수강과목"
          :rules="[{required: true}]"
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
          :rules="[{required: true}]"
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
        <el-button type="primary" @click="studentAddApi(formRef)">등록</el-button>
      </el-form-item>
    </el-form>
  </el-container>
</template>

<script setup lang="ts">
import {reactive, ref} from "vue";
import {COURSE} from "/src/js/course";
import {GRADE} from "/src/js/grade";
import type {FormInstance} from "element-plus";
import axios from "axios";
import router from "@/router";

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

const studentAddApi = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate((valid) => {
    if (valid) {
      axios.post("/api/students", student)
          .then(res => router.replace({ name: "students"}))
          .catch((err) => {
            const result = err.response.data;
            alert(result.message);
          });
      alert("submit!")
    } else {
      alert("error!")
      return false
    }
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
