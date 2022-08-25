<template>
  <el-container class="teacherInfoContainer">
    <el-form
        ref="form"
        :model="teacher"
        label-width="auto"
        label-position="right"
        size="default"
    >
      <el-form-item label="이름">
        <el-input v-model="teacher.name" />
      </el-form-item>
      <el-form-item label="연락처">
        <el-input v-model="teacher.phoneNumber" />
      </el-form-item>
      <el-form-item label="이메일">
        <el-input v-model="teacher.email" />
      </el-form-item>
      <el-form-item label="담당과목">
        <el-select v-model="teacher.course">
          <el-option
              v-for="course in COURSE"
              :key="course.label"
              :label="course.label"
              :value="course.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="등록일">
        {{teacher.createAt}}
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="editTeacherApi">수정</el-button>
        <el-button type="danger" @click="deleteTeacherApi">삭제</el-button>
      </el-form-item>
    </el-form>
    <el-timeline>
      <h1>스케줄</h1>
      <el-timeline-item
          v-for="schedule in schedules"
          :key="schedule.id"
          :timestamp="schedule.start"
          :color="schedule.stateColor"
      >
        {{`${schedule.studentName} / ${GRADE[schedule.grade].label}`}}
      </el-timeline-item>
    </el-timeline>
  </el-container>
</template>

<script setup lang="ts">
import {useTeacherStore} from "@/store/teacherStore";
import {onMounted, ref} from "vue";
import {COURSE} from "/src/js/course";
import {GRADE} from "/src/js/grade";
import axios from "axios";
import router from "@/router";

const schedules = ref([]);
const teacherStore = useTeacherStore();
const teacher = ref({...teacherStore.getTeacher})

const editTeacherApi = () => {
  axios.patch(`/api/teachers/${teacher.value.id}`, teacher.value)
      .then(res => {
        teacherStore.set(teacher.value);
        alert("선생님 정보가 변경되었습니다.");
      })
      .catch((err) => {
        const result = err.response.data;
        alert(result.message);
      });
}

const deleteTeacherApi = () => {
  if (confirm("수강생 정보를 삭제하시겠습니까?")) {
    axios.delete(`/api/teachers/${teacher.value.id}`)
        .then(res => {
          alert("수강생 정보가 삭제되었습니다.");
          router.replace({ name: "teachers"})
        })
        .catch((err) => {
          const result = err.response.data;
          alert(result.message);
        });
  }
}

onMounted(() => {
  schedules.value = [];
  axios.get(`/api/teachers/${teacherStore.getTeacher.id}/schedules`)
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
.teacherInfoContainer {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0;
}

.teacherInfoContainer .el-form {
  width: 500px;
  margin-right: 20px;
}

.teacherInfoContainer .el-timeline {
  width: 200px;
}
</style>
