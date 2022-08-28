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
        <el-form-item label="수강생" prop="studentId">
          <el-select v-model="scheduleForm.studentId" placeholder="수강생" @click="getStudentsApi" :disabled="!scheduleFormState">
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
              label="스케쥴 날짜"
              placeholder="스케쥴 날짜"
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
        <el-form-item label="진행상태" v-if="!scheduleFormState">
          <el-radio-group v-model="scheduleForm.scheduleState">
            <el-radio v-for="state in STATE" :label="state.value">
              {{state.label}}
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="apiCall">{{`${scheduleFormState ? '등록' : '수정'}`}}</el-button>
        <el-button @click="deleteApi" v-if="!scheduleFormState">취소</el-button>
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

const getWeekText = ref(() => {
  return getWeekOfMonth(searchForm.value.appointmentTime) === 0 ?
      `${searchForm.value.appointmentTime.getMonth() === 0 ? 12 : searchForm.value.appointmentTime.getMonth()}월 ` + '5주차'
      : `${searchForm.value.appointmentTime.getMonth() + 1}월 ${getWeekOfMonth(searchForm.value.appointmentTime)}주차`
});
const searchForm = ref({
  course: COURSE.PIANO.value,
  appointmentTime: new Date()
})
const schedules = ref([]);
const weekText = ref(getWeekText.value());
const dialogFormVisible = ref(false);
const scheduleFormState = ref(false);   // true : 등록, false : 변경
const scheduleForm = reactive({
  calendarId: 'mainCalendar',
  scheduleId: '',
  studentId: '',
  teacherId: '',
  start: '',
  end: '',
  scheduleState: ''
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
    gridSelection: {
      enableDblClick: false,
      enableClick: false,
    },
  };
  mainCalendar = new Calendar('#mainCalendar', options);
};

const getCourseList = (course) => {
  searchForm.value.course = course;
  searchForm.value.appointmentTime = new Date();
  scheduleForm.scheduleId = '';
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
              appointmentTime: localDateTimeFormatter(searchForm.value.appointmentTime)
      }})
      .then(res => {
        schedules.value = [];
        mainCalendar.clear();
        weekText.value = getWeekText.value();
        res.data.contents.forEach(schedule => {
          schedules.value.push({
            id: schedule.id,
            calendarId: scheduleForm.calendarId,
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
        const schedule = res.data;
        mainCalendar.createEvents([{
          id: schedule.id,
          calendarId: scheduleForm.calendarId,
          title: schedule.studentName,
          location: schedule.teacherName,
          start: schedule.start,
          end: schedule.end,
          backgroundColor: schedule.state === STATE.NOT_STARTED.value ? "#03bd9e" : "#FF6D6A",
          body: schedule
        }]);
        dialogFormVisible.value = false;
      })
      .catch(err => {
        const result = err.response.data;
        alert(result.message);
      });
}

const patchApi = () => {
  axios.patch(`/api/schedules/${scheduleForm.scheduleId}`, {
    studentId: scheduleForm.studentId,
    teacherId: scheduleForm.teacherId,
    start: localDateTimeFormatter(scheduleForm.start),
    end: localDateTimeFormatter(scheduleForm.end),
    scheduleState: scheduleForm.scheduleState
  })
      .then(res => {
        if (dialogFormVisible.value) alert("스케줄이 변경되었습니다.");

        // 변경된 스케쥴을 가져온다.
        const schedule = res.data;
        mainCalendar.updateEvent(
            scheduleForm.scheduleId,
            scheduleForm.calendarId,
            {
              location: schedule.teacherName,
              start: schedule.start,
              end: schedule.end,
              backgroundColor: schedule.state === STATE.NOT_STARTED.value ? "#03bd9e" : "#FF6D6A",
              body: schedule
            }
        );
        dialogFormVisible.value = false;
      })
      .catch(err => {
        const result = err.response.data;
        alert(result.message);
      });
}

const deleteApi = () => {
  if (!confirm("스케줄을 취소하시겠습니까?")) return;
  axios.delete(`/api/schedules/${scheduleForm.scheduleId}`)
      .then(res => {
        alert("스케줄이 삭제되었습니다.");
        mainCalendar.deleteEvent(scheduleForm.scheduleId, scheduleForm.calendarId);
        scheduleForm.scheduleId = '';
        scheduleForm.studentId = '';
        scheduleForm.teacherId = '';
        scheduleForm.start = '';
        scheduleForm.end = '';
        scheduleForm.scheduleState = '';
        dialogFormVisible.value = false;
      })
      .catch(err => {
        const result = err.response.data;
        alert(result.message);
      });
}

const apiCall = () => {
  if (scheduleFormState.value) postApi();
  else patchApi();
}

onMounted(() => {
  createMainCalendar();
  mainCalendar
      .on('clickEvent', event => {
        getStudentsApi();
        getTeachersApi();
        const schedule = event.event.body;

        scheduleForm.scheduleId = schedule.id;
        scheduleForm.studentId = schedule.studentId;
        scheduleForm.teacherId = schedule.teacherId;
        scheduleForm.start = event.event.start;
        scheduleForm.end = event.event.end;
        scheduleForm.scheduleState = STATE[schedule.state].value;
        scheduleFormState.value = false;
        dialogFormVisible.value = true;
      })
      .on('selectDateTime', (select) => {
        scheduleForm.scheduleId = '';
        scheduleForm.studentId = '';
        scheduleForm.teacherId = '';
        scheduleForm.start = select.start;
        scheduleForm.end = select.end;
        scheduleFormState.value = true;
        dialogFormVisible.value = true;
      })
      .on('beforeUpdateEvent', ({event, changes}) => {
        console.log(changes.start);
        console.log(changes.end);

        scheduleForm.scheduleId = event.id;
        scheduleForm.studentId = event.body.studentId;
        scheduleForm.teacherId = event.body.teacherId;
        scheduleForm.start = changes.start? changes.start : event.start;
        scheduleForm.end = changes.end;
        scheduleForm.scheduleState = event.body.state;
        patchApi();
      })

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
