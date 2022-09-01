package ruby.api.response.teacher;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ruby.core.domain.Student;
import ruby.core.domain.Teacher;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TeachersByCourseResponse {

    private List<TeacherItem> contents;

    public TeachersByCourseResponse(List<Teacher> teachers) {
        this.contents = teachers.stream()
                .map(TeacherItem::new)
                .collect(Collectors.toList());
    }

    @Getter
    static class TeacherItem {
        private Long id;
        private String name;

        public TeacherItem(Teacher teacher) {
            this.id = teacher.getId();
            this.name = teacher.getName();
        }
    }
}
