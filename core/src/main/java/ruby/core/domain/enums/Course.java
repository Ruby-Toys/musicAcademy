package ruby.core.domain.enums;

import lombok.Getter;

@Getter
public enum Course {
    PIANO, VIOLIN, VIOLA, FLUTE, CLARINET, VOCAL;

    public static Course parseCourse(String name) {
        if (name == null || name.isEmpty()) return null;
        return Course.valueOf(name);
    }
}
