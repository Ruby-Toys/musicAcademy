<template>
<!--    <el-calendar ref="calendar">-->
<!--      <template #header="{ date }">-->
<!--        <el-select v-model="value" class="m-2" placeholder="Select">-->
<!--          <el-option-->
<!--              v-for="item in options"-->
<!--              :key="item.value"-->
<!--              :label="item.label"-->
<!--              :value="item.value"-->
<!--              @click="getSchedules(item.value)"-->
<!--          />-->
<!--        </el-select>-->
<!--        <span>{{ date }}</span>-->
<!--        <el-button-group>-->
<!--          <el-button size="small" @click="selectDate('prev-month')">Previous Month</el-button>-->
<!--          <el-button size="small" @click="selectDate('today')">Today</el-button>-->
<!--          <el-button size="small" @click="selectDate('next-month')">Next Month</el-button>-->
<!--        </el-button-group>-->
<!--      </template>-->
<!--    </el-calendar>-->


<!--    <el-select v-model="value" class="m-2" placeholder="Select">-->
<!--      <el-option-->
<!--          v-for="item in courseOptions"-->
<!--          :key="item.value"-->
<!--          :label="item.label"-->
<!--          :value="item.value"-->
<!--          @click="getSchedules(item.value)"-->
<!--      />-->
<!--    </el-select>-->

    <div id="calendar">
    </div>
    <el-button @click="changeCalendar"></el-button>
</template>

<script lang="ts" setup>
import Calendar from '@toast-ui/calendar';
import {onMounted} from "vue";

let calendar;
const createCalendar = () => {
  const container = document.getElementById('calendar');
  const options: object = {
    week: {
      taskView: false,
      eventView: ['time'],
      startDayOfWeek: 1,
      hourStart: 9,
      hourEnd: 23,
    },
    defaultView: 'week',
    timezone: {
      zones: [
        {
          timezoneName: 'Asia/Seoul',
          displayLabel: 'Seoul',
        },
      ],
    },
    // calendars: [
    //   {
    //     id: 'cal1',
    //     name: '개인',
    //     backgroundColor: '#03bd9e',
    //   },
    // ],
  };
  calendar = new Calendar(container, options);

  calendar.createEvents([
    {
      id: 'event1',
      calendarId: 'cal2',
      title: '주간 회의',
      start: '2022-08-23T13:00:00',
      end: '2022-08-23T20:00:00',
      backgroundColor: '#03bd9e'
    },
  ]);
}

const changeCalendar = () => {
  calendar.changeView(calendar.getViewName() === 'month' ? 'week' : 'month');
}

onMounted(() => {
  createCalendar();
})






// const courseOptions = [
//   {
//     value: 'PIANO',
//     label: '피아노',
//   },
//   {
//     value: 'VIOLIN',
//     label: '바이올린',
//   },
//   {
//     value: 'VIOLA',
//     label: '비올라',
//   },
//   {
//     value: 'CELLO',
//     label: '첼로',
//   },
//   {
//     value: 'FLUTE',
//     label: '플루트',
//   },
//   {
//     value: 'VOCAL',
//     label: '성악',
//   },
//   {
//     value: 'FLUTE',
//     label: '플루트',
//   },
// ]
// const value = ref(courseOptions[0].value);
//
// const getSchedules = (course) => {
//   alert(course);
// }

</script>






<style scoped>

.el-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.el-calendar {
  /*height: 2000px;*/
}

#calendar {
  width: 1200px;
  height: 600px;
  border: 1px solid crimson;
}

</style>
