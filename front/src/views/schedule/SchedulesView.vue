<template>
  <el-container class="schedulesContainer">
    <el-aside>
      <div id="asideCalendar">
      </div>
      <el-menu>
        <el-menu-item
            v-for="item in COURSE"
            :key="item.value"
            @click="getSchedules(item.value)"
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
import {COURSE} from "/src/js/course";
import { onMounted } from "vue";
import 'tui-date-picker/dist/tui-date-picker.css';
import 'tui-time-picker/dist/tui-time-picker.css';

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

  mainCalendar.createEvents([
    {
      id: "event1",
      calendarId: "cal2",
      title: "주간 회의",
      start: "2022-08-23T13:00:00",
      end: "2022-08-23T20:00:00",
      backgroundColor: "#03bd9e",
    },
  ]);
};

const calendarPrev = () => {
  mainCalendar.prev();
  // 화면을 넘기고 기간에 맞는 목록을 조회한다.
}
const calendarNext = () => {
  mainCalendar.next();
  // 화면을 넘기고 기간에 맞는 목록을 조회한다.
}

onMounted(() => {
  createAsideCalendar();
  createMainCalendar();
  // 현재 날짜가 속한 주 단위의 기간의 스케쥴을 조회한다.
});

const getSchedules = (course) => {
  alert(course);
};
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
  padding: 20px 50px 100px;
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
