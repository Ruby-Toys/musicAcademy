<template>
  <el-container class="studentInfoContainer">
    <el-form
        ref="form"
        :model="student"
        label-width="auto"
        label-position="right"
        size="default"
    >
      <el-form-item prop="name" label="이름" :rules="nameRule">
        <el-input v-model="student.name" />
      </el-form-item>
      <el-form-item prop="phoneNumber" label="연락처" :rules="phoneNumberRule">
        <el-input v-model="student.phoneNumber" />
      </el-form-item>
      <el-form-item prop="email" label="이메일" :rules="emailRule">
        <el-input v-model="student.email" />
      </el-form-item>
      <el-form-item prop="course" label="수강과목" :rules="courseRule">
        <el-select v-model="student.course">
          <el-option
              v-for="course in COURSE"
              :key="course.label"
              :label="course.label"
              :value="course.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="grade" label="등급" :rules="gradeRule">
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
        <el-button type="primary" @click="patchApi">수정</el-button>
        <el-button type="success" @click="openPaymentPopup">결제하기</el-button>
        <el-button type="danger" @click="deleteApi">삭제</el-button>
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




    <!-- dialog -->
    <el-dialog v-model="dialogFormVisible" title="결제" width="600px">
      <el-form
          :model="student"
          label-width="120px"
          size="small"
          status-icon
      >
        <el-form-item label="수강생">
          {{student.name}}
        </el-form-item>
        <el-form-item label="결제금액">
          <el-input v-model="amount" type="number"/>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button type="success" @click="postPaymentApi">결제</el-button>
      </span>
      </template>
    </el-dialog>
  </el-container>


</template>

<script setup lang="ts">
import {useStudentStore} from "@/store/studentStore";
import {onMounted, reactive, ref} from "vue";
import {COURSE} from "/src/ts/course";
import {GRADE} from "/src/ts/grade";
import axios from "axios";
import router from "@/router";
import {nameRule, phoneNumberRule, emailRule, courseRule, gradeRule } from "/src/validator/FormValidator"

const schedules = ref([]);
const studentStore = useStudentStore();
const student = ref({...studentStore.getStudent})
const dialogFormVisible = ref(false);
const amount = ref(GRADE[student.value.grade].amount);

const openPaymentPopup = () => {
  amount.value = GRADE[student.value.grade].amount;
  dialogFormVisible.value = true;
}

const patchApi = () => {
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

const deleteApi = () => {
  if (confirm("수강생 정보를 삭제하시겠습니까?")) {
    axios.delete(`/api/students/${student.value.id}`)
        .then(res => {
          alert("수강생 정보가 삭제되었습니다.");
          studentStore.delete();
          router.replace({ name: "students"})
        })
        .catch((err) => {
          const result = err.response.data;
          alert(result.message);
        });
  }
}

const postPaymentApi = () => {

  if (confirm("결제를 진행하시겠습니까?")) {
    axios.get("/api/kakaoPay/ready", {
          params: {
            studentId: student.value.id,
            amount: amount.value
          }
        })
        .then((res) => {
          dialogFormVisible.value = false;
          const width = 600;
          const height = 800;
          const popupX = (window.screen.width / 2)   - (width / 2);
          const popupY = (window.screen.height / 2) - (height / 2);

          window.open(res.data.next_redirect_pc_url,'',`width=${width}, height=${height}, left=${popupX}, top=${popupY}`);
        });
  }
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

.el-select {
  width: 100%;
}

.studentInfoContainer .el-timeline {
  width: 200px;
}
</style>
