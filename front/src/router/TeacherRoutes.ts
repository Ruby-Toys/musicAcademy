import TeachersView from "@/views/teacher/TeachersView.vue";
import TeacherInfoView from "@/views/teacher/TeacherInfoView.vue";
import TeacherPostView from "@/views/teacher/TeacherPostView.vue";

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
    },
    {
        path: "/teacherPost",
        name: "teacherPost",
        component: TeacherPostView,
    },
]