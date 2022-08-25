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
  </el-container>
</template>

<script lang="ts" setup>
import Calendar from "@toast-ui/calendar";
import {COURSE} from "/src/ts/course";
import {getWeekOfMonth} from "/src/ts/DateUtils";
import {onMounted, ref} from "vue";
import 'tui-date-picker/dist/tui-date-picker.css';
import 'tui-time-picker/dist/tui-time-picker.css';
import axios from "axios";

const searchForm = ref({
  course: COURSE.PIANO.value,
  appointmentTime: new Date()
})
const schedules = ref([]);
const weekText = ref(`${searchForm.value.appointmentTime.getMonth() + 1}월 ${getWeekOfMonth(searchForm.value.appointmentTime)}주차`);

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
    useFormPopup: true,
    useDetailPopup: true,
  };
  mainCalendar = new Calendar('#mainCalendar', options);
};

const getDateStr = (date) => {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  return  year + (month < 10 ? '-0' : "-") + month + (day < 10 ? '-0' : "-") + day;
}

const getCourseList = (course) => {
  searchForm.value.course = course;
  searchForm.value.appointmentTime = new Date();
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
            title: schedule.studentName + " / " + schedule.teacherName,
            attendees: [schedule.teacherName],
            start: schedule.start,
            end: schedule.end,
            backgroundColor: "#03bd9e",    // Course 에 색상값을 둘 것
            state: schedule.state
          });
        })
        mainCalendar.createEvents(schedules.value);
      })
      .catch(err => {
        const result = err.response.data;
        alert(result.message);
      });
};

onMounted(() => {
  createMainCalendar();
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

#asideCalendar {
  width: 200px;
  height: 200px;
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
</style>
