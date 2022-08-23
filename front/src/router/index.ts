import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import LoginView from "../views/LoginView.vue";
import MainView from "../views/MainView.vue";
import SchedulesView from "../views/schedule/SchedulesView.vue";
import StudentsView from "../views/student/StudentsView.vue";
import TeachersView from "../views/teacher/TeachersView.vue";
import PaymentsView from "../views/payment/PaymentsView.vue";

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
        {
          path: "/schedules",
          name: "schedules",
          component: SchedulesView,
        },
        {
          path: "/students",
          name: "students",
          component: StudentsView,
        },
        {
          path: "/teachers",
          name: "teachers",
          component: TeachersView,
        },
        {
          path: "/payments",
          name: "payments",
          component: PaymentsView,
        },
      ],
    },
  ],
});

export default router;
