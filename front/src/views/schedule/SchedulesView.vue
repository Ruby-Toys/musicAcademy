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
      <div id="mainCalendar">
      </div>
    </el-main>
  </el-container>
</template>

<script lang="ts" setup>
import Calendar from "@toast-ui/calendar";
import {COURSE} from "/src/js/course";
import { onMounted } from "vue";

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
  };
  mainCalendar = new Calendar('#mainCalendar', options);

  // calendar.createEvents([
  //   {
  //     id: "event1",
  //     calendarId: "cal2",
  //     title: "주간 회의",
  //     start: "2022-08-23T13:00:00",
  //     end: "2022-08-23T20:00:00",
  //     backgroundColor: "#03bd9e",
  //   },
  // ]);
};

onMounted(() => {
  createAsideCalendar();
  createMainCalendar();
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
  padding: 50px 50px 100px;
}

#mainCalendar {
  width: 100%;
  height: 100%;
  min-height: 600px;
}
</style>
