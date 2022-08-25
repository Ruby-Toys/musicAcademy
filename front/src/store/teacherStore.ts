import {defineStore} from "pinia";

export const useTeacherStore = defineStore({
    id: "teacher",
    state: () => ({
        teacher: {
            id: null,
            name: '',
            email: '',
            phoneNumber: '',
            course: '',
            createAt: '',
        }
    }),
    getters : {
        getTeacher: (state) => state.teacher
    },
    actions: {
        set(teacher) {
            this.teacher = {...this.teacher, ...teacher}
        },
        delete() {
            this.teacher = {
                id: null,
                name: '',
                email: '',
                phoneNumber: '',
                course: '',
                createAt: '',
            }
        }
    }
})