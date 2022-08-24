import StudentsView from "@/views/student/StudentsView.vue";
import StudentInfoView from "@/views/student/StudentInfoView.vue";
import StudentAddView from "@/views/student/StudentAddView.vue";

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
        path: "/studentCreate",
        name: "studentCreate",
        component: StudentAddView,
    },
]