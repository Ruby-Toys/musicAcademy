package ruby.core.domain.enums;

import lombok.Getter;

@Getter
public enum Grade {
    BEGINNER(150000), INTERMEDIATE(180000), ADVANCED(200000);

    long amount;

    Grade(long amount) {
        this.amount = amount;
    }

    public static Grade parseGrade(String name) {
        if (name == null || name.isEmpty()) return null;
        return Grade.valueOf(name);
    }
}
