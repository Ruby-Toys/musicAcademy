<template>
  <el-container class="studentInfoContainer">
    <el-form
        ref="form"
        :model="student"
        label-width="auto"
        label-position="right"
        size="default"
    >
      <el-form-item label="이름">
        <el-input v-model="student.name" />
      </el-form-item>
      <el-form-item label="연락처">
        <el-input v-model="student.phoneNumber" />
      </el-form-item>
      <el-form-item label="이메일">
        <el-input v-model="student.email" />
      </el-form-item>
      <el-form-item label="수강과목">
        <el-select v-model="student.course">
          <el-option
              v-for="course in COURSE"
              :key="course.label"
              :label="course.label"
              :value="course.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="등급">
        <el-select v-model="student.grade">
          <el-option
              v-for="grade in GRADE"
              :key="grade.label"
              :label="grade.label"
              :value="grade.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="등록일">
        {{student.createAt}}
      </el-form-item>
      <el-form-item label="기타">
        <el-input v-model="student.memo" type="textarea" resize="false"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="editStudentApi">수정</el-button>
        <el-button type="danger" @click="movePaymentProcess">결제하기</el-button>
      </el-form-item>
    </el-form>
    <el-timeline>
      <h1>이번 달 스케줄</h1>
      <el-timeline-item
          v-for="schedule in schedules"
          :key="schedule.id"
          :timestamp="schedule.start"
          :color="schedule.stateColor"
      >
        {{`담당 선생님: ${schedule.teacherName}`}}
      </el-timeline-item>
    </el-timeline>
  </el-container>


</template>

<script setup lang="ts">
import {useStudentStore} from "@/store/student";
import {onMounted, ref} from "vue";
import {COURSE} from "/src/js/course";
import {GRADE} from "/src/js/grade";
import axios from "axios";
import router from "@/router";

const schedules = ref([]);
const studentStore = useStudentStore();
const student = ref({...studentStore.getStudent})

// TODO - 회원 정보 수정, 결제 페이지 이동
const editStudentApi = () => {
  axios.patch(`/api/students/${student.value.id}`, student.value)
      .then(res => {
        studentStore.set(student.value);
        alert("수강생 정보가 변경되었습니다.");
      })
      .catch((err) => {
        const result = err.response.data;
        alert(result.message);
      });
}

const movePaymentProcess = () => {
  // 결제할 수강생 정보는 store 에 저장되어 있는 정보를 그대로 사용
  router.push({ name: "paymentProcess"});
}

onMounted(() => {
  schedules.value = [];
  axios.get(`/api/students/${studentStore.getStudent.id}/schedules`)
      .then((res) => {
        res.data.contents.forEach(schedule => {
          schedule.stateColor = schedule.state === 'NOT_STARTED' ? 'crimson' : 'green';
          schedules.value.push(schedule);
        })
      })
      .catch(err => {
        const result = err.response.data;
        alert(result.message);
      });
})
</script>


<style scoped>
.studentInfoContainer {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0;
}

.studentInfoContainer .el-form {
  width: 500px;
  margin-right: 20px;
}

.studentInfoContainer .el-timeline {
  width: 200px;
}
</style>
