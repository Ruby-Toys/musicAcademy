<template>
  <el-container class="schedulesContainer">
    <el-aside>
      <el-menu>
        <el-menu-item
            v-for="item in COURSE"
            :key="item.value"
            @click="getCourseList(item.value)"
        >
          {{item.label}}
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-main>
      <div class="calendar-nav">
        <el-button @click="calendarPrev">Prev</el-button>
        <el-button @click="calendarNext">Next</el-button>

        <div>
          {{weekText}}
        </div>
      </div>
      <div id="mainCalendar">
      </div>
    </el-main>

    <!-- dialog -->
    <el-dialog v-model="dialogFormVisible" :title="scheduleFormState ? '스케줄 등록' : '스케줄 수정'" width="600px">
      <el-form
          :model="scheduleForm"
          label-width="120px"
          size="small"
          status-icon
      >
        <!-- 입력 창에서 해당 과목의 수강생 목록과 선생님 목록을 조회해서 선택할 수 있도록 해야한다.-->
        <el-form-item label="수강생" prop="studentId">
          <el-select v-model="scheduleForm.studentId" placeholder="수강생" @click="getStudentsApi">
            <el-option
                v-for="student in courseStudents"
                :key="student.id"
                :label="student.name"
                :value="student.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="선생님" prop="teacherId">
          <el-select v-model="scheduleForm.teacherId" placeholder="선생님" @click="getTeachersApi">
            <el-option
                v-for="teacher in courseTeachers"
                :key="teacher.id"
                :label="teacher.name"
                :value="teacher.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="스케쥴 날짜">
          <el-date-picker
              v-model="scheduleForm.start"
              type="date"
              label="스케쥴 시작 시간"
              placeholder="스케쥴 시작 시간"
              :disabled="scheduleFormState"
          />
        </el-form-item>
        <el-form-item label="스케쥴">
          <el-col :span="11">
            <el-form-item prop="start">
              <el-time-picker
                  v-model="scheduleForm.start"
                  label="스케쥴 시작 시간"
                  placeholder="스케쥴 시작 시간"
                  :disabled="scheduleFormState"
              />
            </el-form-item>
          </el-col>
          <el-col class="text-center" :span="2">
            <span class="text-gray-500">-</span>
          </el-col>
          <el-col :span="11">
            <el-form-item prop="end">
              <el-time-picker
                  v-model="scheduleForm.end"
                  label="스케쥴 종료 시간"
                  placeholder="스케쥴 종료 시간"
                  :disabled="scheduleFormState"
              />
            </el-form-item>
          </el-col>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="apiCall">{{`${scheduleFormState ? '등록' : '수정'}`}}</el-button>
        <el-button id="dialogCloseButton" type="primary" @click="dialogFormVisible = false">Confirm</el-button>
      </span>
      </template>
    </el-dialog>
  </el-container>
</template>

<script lang="ts" setup>
import Calendar from "@toast-ui/calendar";
import {COURSE} from "/src/ts/course";
import {STATE} from "/src/ts/scheduleState";
import {getWeekOfMonth, localDateTimeFormatter} from "/src/ts/DateUtils";
import {onMounted, reactive, ref} from "vue";
import 'tui-date-picker/dist/tui-date-picker.css';
import 'tui-time-picker/dist/tui-time-picker.css';
import axios from "axios";
import router from "@/router";

const searchForm = ref({
  course: COURSE.PIANO.value,
  appointmentTime: new Date()
})
const schedules = ref([]);
const weekText = ref(`${searchForm.value.appointmentTime.getMonth() + 1}월 ${getWeekOfMonth(searchForm.value.appointmentTime)}주차`);
const dialogFormVisible = ref(false);
const scheduleFormState = ref(false);   // true : 등록, false : 변경
const scheduleForm = reactive({
  studentId: '',
  teacherId: '',
  start: '',
  end: '',
})
const courseStudents = ref([]);
const courseTeachers = ref([]);

let mainCalendar;
const createMainCalendar = () => {
  const options: object = {
    week: {
      taskView: false,
      eventView: ["time"],
      dayNames: ['일', '월', '화', '수', '목', '금', '토'],
      startDayOfWeek: 1,
      hourStart: 9,
      hourEnd: 23,
    },
    defaultView: "week",
    timezone: {
      zones: [
        {
          timezoneName: "Asia/Seoul",
          displayLabel: "Seoul",
        },
      ],
    },
    calendars: [
      {
        id: 'mainCalendar',
        name: 'mainCalendar',
      },
    ],
    template: {
      time(event) {
        return `<div style="color: black; font-size: 13px;">Student: ${event.title}</div>`
            + `<div style="color: black; font-size: 13px;">Teacher: ${event.location}</div>`;
      },
    },
  };
  mainCalendar = new Calendar('#mainCalendar', options);
};

const getDateStr = (date) => {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  return year + (month < 10 ? '-0' : "-") + month + (day < 10 ? '-0' : "-") + day;
}

const getCourseList = (course) => {
  searchForm.value.course = course;
  searchForm.value.appointmentTime = new Date();
  scheduleForm.studentId = '';
  scheduleForm.teacherId = '';
  scheduleForm.start = '';
  scheduleForm.end = '';
  courseStudents.value = [];
  courseTeachers.value = [];
  getListApi();
  mainCalendar.today();
}

const calendarPrev = () => {
  const prevDate = searchForm.value.appointmentTime.getDate() - 7;
  searchForm.value.appointmentTime.setDate(prevDate);
  getListApi();
  mainCalendar.prev();
}
const calendarNext = () => {
  const prevDate = searchForm.value.appointmentTime.getDate() + 7;
  searchForm.value.appointmentTime.setDate(prevDate);
  getListApi();
  mainCalendar.next();
}

const getListApi = () => {
  axios.get("/api/schedules",
      {
        params:
            {
              course: searchForm.value.course,
              appointmentTime: getDateStr(searchForm.value.appointmentTime)
      }})
      .then(res => {
        schedules.value = [];
        mainCalendar.clear();
        weekText.value = `${searchForm.value.appointmentTime.getMonth() + 1}월 ${getWeekOfMonth(searchForm.value.appointmentTime)}주차`;
        res.data.contents.forEach(schedule => {
          schedules.value.push({
            id: schedule.id,
            calendarId: 'mainCalendar',
            title: schedule.studentName,
            location: schedule.teacherName,
            start: schedule.start,
            end: schedule.end,
            backgroundColor: schedule.state === STATE.NOT_STARTED.value ? "#03bd9e" : "#FF6D6A",
            body: schedule
          });
        })
        mainCalendar.createEvents(schedules.value);
      })
      .catch(err => {
        const result = err.response.data;
        alert(result.message);
      });
};

const getStudentsApi = () => {
  if (courseStudents.value.length > 0) return;

  axios.get("/api/students/course", {params: {course: searchForm.value.course}})
      .then(res => {
        courseStudents.value = res.data.contents;
      })
      .catch(err => {
        const result = err.response.data;
        alert(result.message);
      });
}

const getTeachersApi = () => {
  if (courseTeachers.value.length > 0) return;

  axios.get("/api/teachers/course", {params: {course: searchForm.value.course}})
      .then(res => {
        courseTeachers.value = res.data.contents;
      })
      .catch(err => {
        const result = err.response.data;
        alert(result.message);
      });
}

const postApi = () => {
  axios.post("/api/schedules", {
        studentId: scheduleForm.studentId,
        teacherId: scheduleForm.teacherId,
        start: localDateTimeFormatter(scheduleForm.start),
        end: localDateTimeFormatter(scheduleForm.end),
      })
      .then(res => {
        alert("스케줄이 등록되었습니다.");
        router.go(0);
      })
      .catch(err => {
        const result = err.response.data;
        alert(result.message);
      });
}

const patchApi = () => {
  alert("patch!");
  // todo - 수정요청 처리
}

const apiCall = () => {
  if (scheduleFormState.value) postApi();
  else patchApi();
}

onMounted(() => {
  createMainCalendar();
  mainCalendar.on('clickEvent', event => {
    getStudentsApi();
    getTeachersApi();
    const schedule = event.event.body;

    scheduleForm.studentId = schedule.studentId;
    scheduleForm.teacherId = schedule.teacherId;
    scheduleForm.start = event.event.start;
    scheduleForm.end = event.event.end;
    scheduleFormState.value = false;
    dialogFormVisible.value = true;
  }).on('selectDateTime', (select) => {
    scheduleForm.start = select.start;
    scheduleForm.end = select.end;
    scheduleFormState.value = true;
    dialogFormVisible.value = true;
  });

  getListApi();
});
</script>

<style scoped>
.schedulesContainer {
  display: flex;
  /*width: 80%;*/
  min-width: 1200px;
  height: 100%;
}

.el-aside {
  width: 210px;
  border-right: 1px solid crimson;
  padding-top: 20px;
}

.el-menu {
  margin-top: 20px;
}

.el-menu-item.is-active {
  color: #303133;
}

.el-main {
  width: calc(100% - 200px);
  padding: 20px 10px 100px 50px;
}

.calendar-nav {
  position: relative;
  display: flex;
  height: 50px;
  align-items: center;
}

.calendar-nav > div {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-size: 30px;
  font-weight: 600;
}

#mainCalendar {
  width: 100%;
  height: calc(100% - 50px);
  min-height: 600px;
}


.dialogButton {
  display: none;
}

.el-button--text {
  margin-right: 15px;
}
.el-select {
  width: 300px;
}
.el-input {
  width: 300px;
}
.text-center {
  display: flex;
  justify-content: center;
}

.dialog-footer button:first-child {
  margin-right: 10px;
}
</style>
