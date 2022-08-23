package ruby.api.response.teacher;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ruby.core.domain.Teacher;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class TeachersResponse {

    private List<TeacherResponse> contents;

    public TeachersResponse(List<Teacher> teachers) {
        this.contents = teachers.stream()
                .map(TeacherResponse::new)
                .collect(Collectors.toList());
    }
}
