package ruby.api.request.student;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StudentSearch {

    private String word;
    private int page;
    public static int PAGE_SIZE = 15;
}
