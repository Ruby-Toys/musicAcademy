package ruby.api.response.student;

import lombok.Getter;
import ruby.core.domain.Student;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class StudentsByCourseResponse {

    private List<StudentItem> contents;

    public StudentsByCourseResponse(List<Student> students) {
        this.contents = students.stream()
                .map(StudentItem::new)
                .collect(Collectors.toList());
    }

    @Getter
    static class StudentItem {
        private Long id;
        private String name;

        public StudentItem(Student student) {
            this.id = student.getId();
            this.name = student.getName();
        }
    }
}
