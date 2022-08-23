import TeachersView from "@/views/teacher/TeachersView.vue";
import TeacherInfoView from "@/views/teacher/TeacherInfoView.vue";

export const teacherRoutes = [
    {
        path: "/teachers",
        name: "teachers",
        component: TeachersView,
    },
    {
        path: "/teacherInfo",
        name: "teacherInfo",
        component: TeacherInfoView,
        props: true
    },
]