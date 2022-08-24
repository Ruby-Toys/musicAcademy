import {defineStore} from "pinia";

export const useStudentStore = defineStore({
    id: "student",
    state: () => ({
        student: {
            id: null,
            name: '',
            email: '',
            phoneNumber: '',
            course: '',
            grade: '',
            createAt: '',
            memo: ''
        }
    }),
    getters : {
        getStudent: (state) => state.student
    },
    actions: {
        set(student) {
            this.student = {...this.student, ...student}
        },
        delete() {
            this.student = {
                id: null,
                name: '',
                email: '',
                phoneNumber: '',
                course: '',
                grade: '',
                createAt: '',
                memo: ''
            }
        }
    }
})