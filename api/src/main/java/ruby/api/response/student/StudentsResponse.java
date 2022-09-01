package ruby.api.response.student;

import lombok.Getter;
import org.springframework.data.domain.Page;
import ruby.core.domain.Student;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class StudentsResponse {

    private List<StudentResponse> contents;
    private int page;
    private long totalCount;
    private int pageSize;

    public StudentsResponse(Page<Student> studentPage) {
        this.contents = studentPage.getContent().stream()
                .map(StudentResponse::new)
                .collect(Collectors.toList());
        this.page = studentPage.getPageable().getPageNumber() + 1;
        this.totalCount = studentPage.getTotalElements();
        this.pageSize = studentPage.getPageable().getPageSize();
    }
}
