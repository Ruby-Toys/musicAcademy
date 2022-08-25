<template>
  <el-container class="schedulesContainer">
    <el-aside>
      <div id="asideCalendar">
      </div>
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
      </div>
      <div id="mainCalendar">
      </div>
    </el-main>
  </el-container>
</template>

<script lang="ts" setup>
import Calendar from "@toast-ui/calendar";
import {COURSE} from "/src/ts/course";
import {onMounted, ref} from "vue";
import 'tui-date-picker/dist/tui-date-picker.css';
import 'tui-time-picker/dist/tui-time-picker.css';
import axios from "axios";

let asideCalendar;
let mainCalendar;
const createAsideCalendar = () => {
  const options: object = {
    month: {
      dayNames: ['일', '월', '화', '수', '목', '금', '토'],
    },
    defaultView: "month",
    timezone: {
      zones: [
        {
          timezoneName: "Asia/Seoul",
          displayLabel: "Seoul",
        },
      ],
    },

  };
  asideCalendar = new Calendar('#asideCalendar', options);
};

const createMainCalendar = () => {
  const options: object = {
    week: {
      taskView: false,
      eventView: ["time"],
      dayNames: ['월', '화', '수', '목', '금', '토', '일'],
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

const calendarPrev = () => {
  mainCalendar.prev();
  // 화면을 넘기고 기간에 맞는 목록을 조회한다.
}
const calendarNext = () => {
  mainCalendar.next();
  // 화면을 넘기고 기간에 맞는 목록을 조회한다.
}

const getTodayStr = () => {
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth() + 1;
  const day = today.getDate();
  return  year + (month < 10 ? '-0' : "-") + month + (day < 10 ? '-0' : "-") + day;
}

const searchForm = ref({
  course: COURSE.PIANO.value,
  appointmentTime: getTodayStr()
})
const schedules = ref([]);

const getCourseList = (course) => {
  searchForm.value.course = course;
  searchForm.value.appointmentTime = getTodayStr();
  getListApi();
}

const getListApi = () => {
  axios.get("/api/schedules", {params: searchForm.value})
      .then(res => {
        schedules.value = [];
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
  createAsideCalendar();
  createMainCalendar();
  // 현재 날짜가 속한 주 단위의 기간의 스케쥴을 조회한다.
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
  height: 50px;
}

#mainCalendar {
  width: 100%;
  height: calc(100% - 50px);
  min-height: 600px;
}
</style>
