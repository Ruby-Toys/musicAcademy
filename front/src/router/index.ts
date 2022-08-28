import {createRouter, createWebHistory} from "vue-router";
import HomeView from "../views/HomeView.vue";
import LoginView from "../views/LoginView.vue";
import MainView from "../views/MainView.vue";

import {scheduleRoutes} from "./ScheduleRoutes"
import {studentRoutes} from "./StudentRoutes"
import {teacherRoutes} from "./TeacherRoutes"
import {paymentRoutes} from "./PaymentRoutes"

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: HomeView,
    },
    {
      path: "/login",
      name: "login",
      component: LoginView,
    },
    {
      path: "/main",
      name: "main",
      component: MainView,
      children: [
          ...scheduleRoutes,
          ...studentRoutes,
          ...teacherRoutes,
          ...paymentRoutes,
      ],
    },
  ],
});



export default router;
