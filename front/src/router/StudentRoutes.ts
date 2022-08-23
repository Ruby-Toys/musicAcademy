import StudentsView from "@/views/student/StudentsView.vue";
import StudentInfoView from "@/views/student/StudentInfoView.vue";

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
        props: true
    },
]