import StudentsView from "@/views/student/StudentsView.vue";
import StudentInfoView from "@/views/student/StudentInfoView.vue";
import StudentPostView from "@/views/student/StudentPostView.vue";

export const studentRoutes = [
    {
        path: "/students",
        name: "students",
        component: StudentsView,
    },
    {
        path: "/studentInfo",
        name: "studentInfo",
        component: StudentInfoView,
    },
    {
        path: "/studentPost",
        name: "studentPost",
        component: StudentPostView,
    },
]